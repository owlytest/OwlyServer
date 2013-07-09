Owly CLient Installaton
==============================


1. Copy the OwlyClnt_Installer_<version>.tar to the server's folder where we want to run the program.

2. Expand the tar file :
	tar xvf OwlyClnt_Installer_<version>.tar

3. Edit properties file with specific configuration of Server to Install (port where it runs). 	

4. Run installer
	- Change binary to 777 --> chmod 777 OwlyClnt_Installer_<version>.bin
	- Execute it : ./OwlyClnt_Installer_<version>.bin
	
5.- Start the client : 
	./OwlyClntd start
