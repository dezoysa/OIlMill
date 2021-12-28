package OilMill;
/*
1. Create single jar by extracting JavaFx library files
2. Give NewMain as the main class of the package
3. Add JavaFx dynamic link libraries to archive
4. Build artifacts and make a singe Jar file with all necessary libraries
5. Run following command at terminal to build the MAC application
 /Library/Java/JavaVirtualMachines/jdk-15.0.1.jdk/Contents/Home/bin/jpackage \
         --type dmg \
         --verbose \
         --input OIlMill_jar \
         --dest output \
         --name OillMill \
         --main-jar OIlMill.jar \
         --main-class OilMill.NewMain \
         --icon OilMill.icns \
        --mac-package-name OilMill \
        --java-options -Xmx2048m
        --app-version 1.00
*/

public class NewMain {
    public static void main(String[] args) {
            Main.main(args);
        }
}
