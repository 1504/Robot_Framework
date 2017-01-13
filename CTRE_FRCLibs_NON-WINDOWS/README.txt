The purpose of this document is to give instruction to manually install
CTRE FRC Libraries.

For Java/C++:

	Take the folder for your desired language (java or cpp) and
	place it into the wpilib user folder.

	In Linux, this is located in USERHOME\wpilib\user.

	If you examine this file directory, you will notice a
	static library in user\cpp\lib (with header in cpp\include)
	and a shared object and jar in user\java\lib.

	The wpilib eclipse plugins will automatically pick up
	any *.a or *.so files in the cpp directory and any
	*.so or *.jar files in the java directory, so any libraries
	can be included this way.


For LabVIEW:
[NOTE: LabVIEW is Windows-only for FRC]
	The Directory for third-party VIs is 
	{LabVIEW Install}\vi.lib\Rock Robotics\WPI\Third Party\[Subfolder]

	There are currently three subfolders: Actuators, CANMotors, and 
	Sensors.  Folders of VIs (that contain a *.mnu file)
	placed in these subfolders will appear in the subpalletes of the
	same name.

	Move LabVIEW\Talon to the CANMotors subfolder and 
	LabVIEW\Pigeon to the Sensors subfolder.


For Robotbuilder:

	These are Robotbuilder extensions for using CTRE classes.
	It currently contains the CAN Talon extension.

	Take the desired extension folder (eg. "CAN Talon") and place it in
	USERHOME\Robotbuilder\extensions.