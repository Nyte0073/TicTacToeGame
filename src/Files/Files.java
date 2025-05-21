package Files;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

public class Files {
    /**Creates a new file, the type according to the
     * ending of the {@code filename} parameter. Outputs whether the file
     * was created or not.
     *  @param filename the name of the file.
     *  @param directory the directory that the file is located in.*/
    public void createFile(String directory, String filename) throws Exception {
        File file = new File(STR."\{directory}\\\{filename}");
        if(file.createNewFile()) {
            System.out.println(STR."\{file.getName()} was created.");
        } else {
            System.out.println(STR."\{file.getName()} was not created.");
        }
    }

    /**Deletes a file corresponding to the inputted directory and filename*/
    public void deleteFile(String directory, String filename) {
        File file = new File(STR."\{directory}\\\{filename}");
        if(file.exists() && file.delete()) {
            System.out.println(STR."\{file.getName()} was deleted.");
        } else if(!file.exists()){
            System.out.println("File was not deleted. File does not exist.");
        } else {
            System.out.println("File was not deleted.");
        }
    }

    /**Allows for editing a .txt file simply by writing lines of  text according to the input
     * of the user. This is STRICTLY for .txt files though and not for any other type of file.*/
    public void editTXTFile(String directory, String filename) throws Exception {
        File file = new File(STR."\{directory}\\\{filename}");
        if(file.exists()) {
            Scanner scanner = new Scanner(System.in);
            FileWriter writer = new FileWriter(file, true);
            while(true) {
                System.out.println("Enter text you want to send to the file.");
                String line = STR."\{scanner.nextLine()}\n";
                writer.write(line);

                System.out.println("Do you want to write another line of text?");
                line = scanner.nextLine();
                if(line.equalsIgnoreCase("yes")) {
                    System.out.println("Ok.");
                } else {
                    break;
                }
            }
            writer.close();
        } else {
            System.out.println("File does not exist.");
        }
    }

    /**Creates a new directory according to value of {@code directoryName}.
     * If the directory wasn't created for any reason, this method will try to create any parent directories
     * that perhaps weren't created and didn't exist before the main directory wanted to be created.*/
    public void createDirectory(String directoryName) throws Exception {
        File file = new File(directoryName);
        if(file.mkdir()) {
            System.out.println("Directory was successfully created.");
        } else {
            System.out.println("Directory wasn't created. Trying to create necessary parent directories...");
            Thread.sleep(2000);

            if(file.mkdirs()) {
                System.out.println("Success! Directory was created.");
            } else {
                System.out.println("Failure in creating directory. Please try again.");
            }
        }
    }
}
