# Jenkins Viewer�v���O�C��

�b�h�i�p���I�C���e�O���[�V�����j�c�[���ł��� [Jenkins CI](http://jenkins-ci.org/) �̃W���u�̏�Ԃ�
�Ď����邽�߂�Eclipse�v���O�C���ł��B

![view1](dist/view1.png)

**����**
* �ז��ɂȂ�Ȃ��B�K�v�ȏ��݂̂��R���p�N�g�Ƀr���[�ɂ܂Ƃ߂܂����B
* �W���u���t�B���^�[�ł���B�Ď��������W���u�݂̂�\���ł���悤�Ƀt�B���^�[�@�\��p�ӂ��܂����B
* �_�u���N���b�N�ɂ��A���΂₭�u���E�U���N����Jenkins�ɃA�N�Z�X�ł��܂��B

## �_�E�����[�h

�ŐV�o�[�W�����@[JenkinsViewer_0.2.0.201302162217.jar](https://github.com/jgoza25/jenkinsViewer/raw/master/dist/JenkinsViewer_0.2.0.201302162217.jar)

## �g����

### �C���X�g�[��
��L���v���O�C����jar�t�@�C�����_�E�����[�h���Aeclipse�̃C���X�g�[���t�H���_��dropins�t�H���_�ɃR�s�[���邾���ł��B

�������AEclipse�̍ċN�����K�v�ł��B

### �ݒ�
1. ���j���[��� �E�B���h�E > �ݒ� ���J���AJenkins Viewer��I�����܂��B
2. �Ď��Ώۂ�Jenkins URL�i��Fhttp://localhost:8080/jenkins�j��ݒ肵�܂��B
3. ���j���[��� �E�B���h�E > �r���[�̕\�� > ���̑� ��I�����܂��B
4. Jenkins > Jenkins View��I�����܂��B 
5. Jenkins Viewer���\������܂��B

![view2](dist/view2.png)


### �t�B���^�[
�t�B���^�[�@�\���g���Ηl�X�ȃW���u��\���ł��܂��B

#### �W���u���Ńt�B���^�[����

name=[job��]

job���ɐ��K�\���̗��p�\�B

* ��jintra����n�܂�W���u������\���������ꍇ�@`name=intra.*1` 
* ��jjob1, job2������\���������ꍇ `name=job1|job2`

#### �r���h���ʂŃt�B���^�[����

result=[�r���h����]

* ��j���s�����r���h������\���������ꍇ `result=FAILURE`


