import java.io.*;
import java.util.*;

class SingleBlock {
    private boolean validBit;
    private int tag;
    private boolean dirtyBit;
    private String[] data;
    public SingleBlock() {
        validBit = false;
        tag = 0;
        dirtyBit = false;
        data = new String[64];
    }
    public boolean getValidBit() { return this.validBit; }
    public int getTag() { return this.tag; }
    public boolean getDirtyBit() { return this.dirtyBit; }
    public String[] getData() { return this.data; }
    public void setValidBit(boolean newBit) { this.validBit = newBit; }
    public void setTag(int newTag) { this.tag = newTag; }
    public void setDirtyBit(boolean dirty) { this.dirtyBit = dirty; }
    public void setData(String[] newData) { this.data = newData; }
}

public class cachesim {
    public static String[] mainMemory = new String[(int)(Math.pow(2,16))];

    public static void main(String args[]) throws Exception {
        String fName = args[0];
        int cacheSize = (int)Math.pow(2, 10)*Integer.parseInt(args[1]);
        int associativity = Integer.parseInt(args[2]);
        String writeType = args[3];
        int blockSizeBytes = Integer.parseInt(args[4]);
        int sets = (cacheSize/blockSizeBytes)/associativity;
        SingleBlock[][] cache = new SingleBlock[sets][associativity];
        for (int i = 0; i < cache.length; i++) {
            for (int j = 0; j < cache[i].length; j++) {
                cache[i][j] = new SingleBlock();
            }
        }

        File file = new File(fName);

        Scanner sc = new Scanner(file);

        while(sc.hasNextLine()) {
            String eachLine = sc.nextLine();

            String type = getType(eachLine);
            String address = getAddy(eachLine);
            int size = Integer.parseInt(getSize(eachLine));

            int addressTen = Integer.parseInt(address, 16);  //convert hex address to base 10 address
            int beginningOfBlock = addressTen - (addressTen%blockSizeBytes);
            int set = (beginningOfBlock/blockSizeBytes)%sets;
            int tag = beginningOfBlock/(sets*blockSizeBytes);
            int bOffset = addressTen%blockSizeBytes;

            if(type.equals("store")) {
                System.out.print("store ");
                System.out.print(address);
                String nudfs = "";
                String dataToWrite = getValue(eachLine);
                char[] dataArray = dataToWrite.toCharArray(); //['c', '7', '7', 'e']
                String[] tempData = new String[size];
                int j = 0;
                for(int l=0; l<dataArray.length; l+=2) {
                    String s = Character.toString(dataArray[l]);
                    String b = Character.toString(dataArray[l+1]);
                    tempData[j] = s+b; //["c7", "7e"]
                    j++;
                }

                for(int k=0; k<cache[set].length; k++) {
                    //if the set entry is valid and the tags are equals
                    if(cache[set][k].getValidBit() && cache[set][k].getTag() == tag) {
                        nudfs = " hit";
                        System.out.println(" hit");
                        for(int m=0; m<tempData.length; m++) {
                            cache[set][k].getData()[bOffset] = tempData[m];
                            bOffset++;
                        }

                        if(writeType.equals("wt")) {
                            for(int n=0; n<blockSizeBytes; n++) {
                                mainMemory[beginningOfBlock] = cache[set][k].getData()[n];
                                beginningOfBlock++;
                            }
                        }
                        else if(writeType.equals("wb")) {
                            cache[set][k].setDirtyBit(true);
                        }
                        //Moves the block that we just used to the front of the set that it belongs to
                        SingleBlock temp = cache[set][k];
                        for (int i = (k - 1); i >= 0; i--) {
                            cache[set][i+1] = cache[set][i];
                        }
                        cache[set][0] = temp;
                    }
                    else if (k == cache[set].length-1 && !nudfs.equals(" hit")) {
                        System.out.println(" miss");
                        if(writeType.equals("wt")) {
                            for(int b=0; b<tempData.length; b++) {
                                mainMemory[addressTen] = tempData[b];
                                addressTen++;
                            }
                        }
                        else if(writeType.equals("wb")) {
                            if(cache[set][associativity-1].getDirtyBit() && cache[set][associativity-1].getValidBit()) {
                                int addressDirtyBlock = cache[set][associativity-1].getTag()*sets*blockSizeBytes+set*blockSizeBytes;
                                for(int a=0; a<blockSizeBytes; a++) {
                                    mainMemory[addressDirtyBlock] = cache[set][associativity-1].getData()[a];
                                    addressDirtyBlock++;
                                }
                            }
                            //Read data from lower memory into cache block
                            String[] str = new String[64];
                            for(int q=0; q<blockSizeBytes; q++) {
                                str[q] = mainMemory[beginningOfBlock];
                                beginningOfBlock++;
                            }
                            cache[set][associativity-1].setData(str);
                            cache[set][associativity-1].setTag(tag);
                            cache[set][associativity-1].setValidBit(true);

                            //Write new data into cache block
                            for(int m=0; m<tempData.length; m++) {
                                cache[set][associativity-1].getData()[bOffset] = tempData[m];
                                bOffset++;
                            }

                            //Mark cache block as dirty
                            cache[set][associativity-1].setDirtyBit(true);

                            //Moving the new block in the cache to the front
                            SingleBlock temp1 = cache[set][k];
                            for (int i = (k - 1); i >= 0; i--) {
                                cache[set][i+1] = cache[set][i];
                            }
                            cache[set][0] = temp1;
                        }
                    }
                }
            }
            if(type.equals("load")) {
                System.out.print("load ");
                System.out.print(address);
                String beyt = "";
                for(int v=0; v<cache[set].length; v++) {
                    //if the set entry is valid and the tags are equal
                    if (cache[set][v].getValidBit() && cache[set][v].getTag() == tag) {
                        beyt = " hit ";
                        System.out.print(" hit ");
                        for(int x=0; x<size; x++) {
                            if(cache[set][v].getData()[bOffset] == null) {
                                cache[set][v].getData()[bOffset] = "00";
                            }
                            System.out.print(cache[set][v].getData()[bOffset]);
                            bOffset++;
                        }
                        System.out.println("");

                        SingleBlock temp4 = cache[set][v];
                        for (int i = (v - 1); i >= 0; i--) {
                            cache[set][i+1] = cache[set][i];
                        }
                        cache[set][0] = temp4;
                    }
                    else if (v == cache[set].length-1 && !beyt.equals(" hit ")) {
                        System.out.print(" miss ");
                        if(writeType.equals("wt")) {
                            String[] str = new String[64];
                            for(int q=0; q<blockSizeBytes; q++) {
                                str[q] = mainMemory[beginningOfBlock];
                                beginningOfBlock++;
                            }
                            cache[set][associativity-1].setData(str);
                            cache[set][associativity-1].setTag(tag);
                            cache[set][associativity-1].setValidBit(true);
                            cache[set][associativity-1].setDirtyBit(false);

                            for(int x=0; x<size; x++) {
                                if(cache[set][v].getData()[bOffset] == null) {
                                    cache[set][v].getData()[bOffset] = "00";
                                }
                                System.out.print(cache[set][associativity-1].getData()[bOffset]);
                                bOffset++;
                            }
                            System.out.println("");

                            SingleBlock temp2 = cache[set][v];
                            for (int i = (v - 1); i >= 0; i--) {
                                cache[set][i+1] = cache[set][i];
                            }
                            cache[set][0] = temp2;
                        }
                        else if(writeType.equals("wb")) {
                            if(cache[set][associativity-1].getDirtyBit() && cache[set][associativity-1].getValidBit()) {
                                int addressDirtyBlock = cache[set][associativity-1].getTag()*sets*blockSizeBytes+set*blockSizeBytes;
                                for(int a=0; a<blockSizeBytes; a++) {
                                    mainMemory[addressDirtyBlock] = cache[set][associativity-1].getData()[a];
                                    addressDirtyBlock++;
                                }
                            }
                            String[] str = new String[64];
                            for(int q=0; q<blockSizeBytes; q++) {
                                str[q] = mainMemory[beginningOfBlock];
                                beginningOfBlock++;
                            }
                            cache[set][associativity-1].setData(str);
                            cache[set][associativity-1].setTag(tag);
                            cache[set][associativity-1].setValidBit(true);
                            cache[set][associativity-1].setDirtyBit(false);

                            for(int x=0; x<size; x++) {
                                if(cache[set][v].getData()[bOffset] == null) {
                                    cache[set][v].getData()[bOffset] = "00";
                                }
                                System.out.print(cache[set][v].getData()[bOffset]);
                                bOffset++;
                            }
                            System.out.println("");

                            SingleBlock temp3 = cache[set][v];
                            for (int i = (v - 1); i >= 0; i--) {
                                cache[set][i+1] = cache[set][i];
                            }
                            cache[set][0] = temp3;
                        }
                    }
                }
            }
        }
    }

    public static String getType(String line) {
        Scanner temp = new Scanner(line);
        String insnType = temp.next();
        return insnType;
    }

    public static String getAddy(String line) {
        Scanner temp = new Scanner(line);
        temp.next();
        String addy = temp.next();
        return addy;
    }

    public static String getSize(String line) {
        Scanner temp = new Scanner(line);
        temp.next();
        temp.next();
        String sizeBytes = temp.next();
        return sizeBytes;
    }

    public static String getValue(String line) {
        Scanner temp = new Scanner(line);
        temp.next();
        temp.next();
        temp.next();
        String destinationAddy = temp.next();
        return destinationAddy;
    }
}
