package Files;

import java.io.File;

public class CSV {
    private final String filename, directory;
    private final File file;
    public CSV(String filename, String directory) throws Exception {
        this.filename = filename;
        this.directory = directory;
        file = new File(STR."\{filename}\\\{directory}");
    }

    public void createFile() throws Exception {
        if(!file.exists() && file.createNewFile()) {
            System.out.println(STR."\{file.getName()} was created.");
        } else {
            System.out.println(STR."The file \{filename} was not created and/or the file did not exist. Please check if there is an existing location for the file.");
        }
    }

    public void deleteFile() {
        if(file.exists() && file.delete()) {
            System.out.println(STR."The file \{file.getName()} was deleted.");
        } else {
            System.out.println(STR."The file \{filename} was not deleted. Please check if the file exists.");
        }
    }
}
