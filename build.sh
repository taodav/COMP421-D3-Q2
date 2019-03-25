SOURCE_DIRECTORY="src"
BUILD_DIRECTORY="build"
CLASS_DIRECTORY="$BUILD_DIRECTORY/classes"
NAME="LibraryApp"
JAVA_PATH="/opt/ibm/db2/V10.5/java"
JDK_PATH="$JAVA_PATH/jdk64/bin"

if [ ! -d "$BUILD_DIRECTORY" ]; then
    mkdir "$BUILD_DIRECTORY"
fi

if [ ! -d "$CLASS_DIRECTORY" ]; then
    mkdir "$CLASS_DIRECTORY"
fi

$JDK_PATH/javac -sourcepath $SOURCE_DIRECTORY -classpath postgresql-42.2.5.jre7.jar -d $CLASS_DIRECTORY $SOURCE_DIRECTORY/com/library/*.java