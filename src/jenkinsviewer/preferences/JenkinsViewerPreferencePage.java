package jenkinsviewer.preferences;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import jenkinsviewer.Activator;
import jenkinsviewer.views.JenkinsView;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */

public class JenkinsViewerPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public JenkinsViewerPreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("A demonstration of a preference page implementation");
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
		addField(new StringFieldEditor(PreferenceConstants.P_URL, 
				"Jenkins &URL:", getFieldEditorParent()));
		
		IntegerFieldEditor field = new IntegerFieldEditor(
				PreferenceConstants.P_INTERVAL,
				"&Interval(min):",
				getFieldEditorParent());
		field.setValidRange(1, 100);
		addField(field);
		addField(new StringFieldEditor(PreferenceConstants.P_FILTER1, 
				"&Job Filter:", getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.P_FILTER2, 
				"&Job Filter:", getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.P_FILTER3, 
				"&Job Filter:", getFieldEditorParent()));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}
	
	@Override
	public boolean performOk() {
		JenkinsView.pref = true;
		return super.performOk();
	}
	
}