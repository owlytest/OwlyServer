#!/bin/bash

#---------------------------CONSTANTS----------------------

DEBUG=1
TRUE=1
FALSE=0
FUNCT_ERROR=1
FUNCT_NOERROR=0

ERROR=$FALSE

PROPS=OwlySrv.install.properties
PWD=`pwd`
DATE=`date`
DATE_SHORT=`date +"%Y-%m-%d__%H:%M"`

#---------------------------FUNCTIONS----------------------

check_prop()
{
	echo -n "Checking $1... "
	if [ -z "$2" ]
	then
		echo "Error: $1 not set in $PROPS"
		exit 1
	fi
	echo "$2"
}


#-----------------------------------------MAIN-------------------------------------------------------

if [ ! -e $PROPS ]
then
	echo "Error: $PROPS not found"
	exit 1
fi


echo ""
echo "OwlyClnt self extracting installer"
echo "=================================="
echo ""

Act_Dir=`pwd`

# Mandatory keys
#OWLYCLNTPORT=`grep '^OwlyClnt.port' $PWD/$PROPS | awk -F\= '{print $2}'`
#check_prop DimStat.port $OWLYCLNTPORT

#Check version to install


echo ""
echo "Installing OwlySrv"
echo ""


# Create and extract install files to $TMPDIR
echo ""
echo -n "Creating temp dir and extracting install files to it..."
export TMPDIR=`mktemp -d /tmp/selfextract.XXXXXX`
ARCHIVE=`awk '/^__ARCHIVE_BELOW__/ {print NR + 1; exit 0; }' $0`
tail -n+$ARCHIVE $0 | tar xz -C $TMPDIR
echo "Done"

#Copy files to the folder to install in folder where installation is executed.
cp -rp $TMPDIR/*  $Act_Dir

#Change to 777 to be able to start
cd $Act_Dir/apache-*/bin
nbsh=`find . -name "*sh" | wc -l`
if [ $nbsh -ne 0 ]; then
##19-10-2012 Line added to execute dos2unix 
	find . -name "*sh" | xargs sed -i 's/\r//'
    find . -name "*sh" | xargs chmod 777
fi

#Remove Installation files
cd $Act_Dir
rm -f OwlySrv_Installer*
rm OwlySrv.install.properties
rm README.txt


#Remove $TMPDIR
echo -n "Removing $TMPDIR..."
rm -rf $TMPDIR
echo "Done"

echo ""
echo "Installation done"
echo ""

exit 0

__ARCHIVE_BELOW__