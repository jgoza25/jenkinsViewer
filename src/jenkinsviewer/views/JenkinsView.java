package jenkinsviewer.views;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import jenkinsviewer.Activator;
import jenkinsviewer.preferences.PreferenceConstants;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.internal.browser.WorkbenchBrowserSupport;
import org.eclipse.ui.part.*;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class JenkinsView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "jenkinsviewer.views.JenkinsView";

	private TableViewer viewer;

	/*
	 * The content provider class is responsible for
	 * providing objects to the view. It can wrap
	 * existing objects in adapters or simply return
	 * objects as-is. These objects may be sensitive
	 * to the current input of the view, or ignore
	 * it and always show the same content 
	 * (like Task List, for example).
	 */
	 
	class ViewContentProvider implements IStructuredContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}
		public void dispose() {
		}
		public Object[] getElements(Object parent) {
			return result.values().toArray();
		}
	}
	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			Map<String, String> job = (Map<String, String>)obj;
			return getText(job.get("name") + "  " + job.get("elapsed"));
		}
		public Image getColumnImage(Object obj, int index) {
			Map<String, String> job = (Map<String, String>)obj;
			String color = job.get("color");
			ImageDescriptor id = null;
			if (color.startsWith("blue")) {
				id = Activator.getImageDescriptor("icons/blue.png");
			}
			if (color.startsWith("red")) {
				id = Activator.getImageDescriptor("icons/red.png");
			}
			if (color.startsWith("yellow")) {
				id = Activator.getImageDescriptor("icons/yellow.png");
			}
			if (id == null) {
				id = Activator.getImageDescriptor("icons/grey.png");
			}
			return id.createImage();
		}
	}
	class NameSorter extends ViewerSorter {
	}

	/**
	 * The constructor.
	 */
	public JenkinsView() {
		doTimer();
	}
	
	private static Map<Node,Map<String,String>> result = new ConcurrentHashMap<Node, Map<String, String>>();

	// タイマーの実行メソッド
	public void doTimer() {
		Runnable runnable = new Runnable() {
			public void run() {
				while (true) {
					cacheElements();
					Display.getDefault().asyncExec(new InnerTimerThread());
					IPreferenceStore store = Activator.getDefault().getPreferenceStore();
					int interval = store.getInt(PreferenceConstants.P_INTERVAL);
					try {
						Thread.sleep(interval * 60 * 1000);
					} catch (InterruptedException e) {
					}
				}
			};
		};
		Thread a = new Thread(runnable);
		a.start();
	}
	
	public void cacheElements() {
		try {
			result.clear();
			IPreferenceStore store = Activator.getDefault().getPreferenceStore();
			String p_url = store.getString(PreferenceConstants.P_URL);
			URL url = new URL(p_url + "/api/xml?depth=2");
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
			        .newDocumentBuilder();
			Document doc = builder.parse(url.openStream());
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			NodeList entries = (NodeList) xpath.evaluate(
	                "/hudson/job/build[1]/timestamp", doc, XPathConstants.NODESET );
	        for( int i = 0; i < entries.getLength(); i++ ) {
	        	Node jobNode = entries.item(i).getParentNode().getParentNode();
        		Map<String, String> job = result.get(jobNode);
        		if (job == null) { 
        			job = new HashMap<String, String>();
        		}
	        	for (int j = 0; j < jobNode.getChildNodes().getLength(); j++) {
	        		Node n = jobNode.getChildNodes().item(j);
	        		job.put(n.getNodeName(), n.getTextContent());
	        	}
	        	jobNode = entries.item(i).getParentNode();
	        	for (int j = 0; j < jobNode.getChildNodes().getLength(); j++) {
	        		Node n = jobNode.getChildNodes().item(j);
	        		job.put(n.getNodeName(), n.getTextContent());
	        	}
	        	job.put("elapsed", getElapsedTime(job.get("timestamp")));
	        	result.put(jobNode, job);
	        }
			String p_filter = store.getString(PreferenceConstants.P_FILTER);
			
			if (p_filter.contains("=")) {
		        String f_key = p_filter.substring(0, p_filter.indexOf("="));
		        String f_value = p_filter.substring(p_filter.indexOf("=") + 1);
		        for (Entry<Node, Map<String, String>> entity : result.entrySet()) {
		        	if (entity.getValue().get(f_key) != null && !entity.getValue().get(f_key).matches(f_value)) {
		        		result.remove(entity.getKey());
		        	}
		        }
			}
	        
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}
	
	private String getElapsedTime(String timestamp) {
    	Date elapsed = new Date(Long.parseLong(timestamp));
    	long sec = ((new Date()).getTime() - elapsed.getTime()) / 1000;
    	
    	if (sec < 60) {
    		return "Just now";
    	} else if (sec < 60 * 60) {
    		int mins = (int) (sec / 60);
    		return String.format("%dmin", mins) + (mins == 1 ? "" : "s");
    	} else if (sec < 60 * 60 * 24) {
    		int hours = (int) (sec / 60 / 60);
    		return String.format("%dhr", hours) + (hours == 1 ? "" : "s");
    	} else if (sec < 60 * 60 * 24 * 30) {
    		int days = (int) (sec / 60 / 60 / 24);
    		return String.format("%dday", days) + (days == 1 ? "" : "s");
    	} else {
    		int months = (int) (sec / 60 / 60 / 24 / 30);
    		return String.format("%dmonth", months) + (months == 1 ? "" : "s");
    	}
	}
	// タイマー内で使われる内部スレッドクラス
	class InnerTimerThread implements Runnable {
		public void run() {
			viewer.setInput(getViewSite());
		}
	}
	
	
	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setSorter(new NameSorter());
		viewer.setInput(getViewSite());

		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "JenkinsViewer.viewer");
		hookDoubleClickAction();
	}
	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				Map<String, String> map = (Map<String, String>)((StructuredSelection)event.getSelection()).getFirstElement();
				try {
				    IWebBrowser browser = PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser();
				    browser.openURL(new URL(map.get("url")));
				} catch (Exception e) {
				    e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}