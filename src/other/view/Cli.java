package other.view;

import other.Messages;
import other.UserInputType;
import other.controller.Controller;

import java.io.IOException;
import java.util.Scanner;

public class Cli implements UI {
    Scanner in = new Scanner(System.in);
    private Controller controller;
    private int selectedStructure;
    private String filename;

    public void initView(){
        Scanner in = new Scanner(System.in);
        boolean end = false;
        while(!end){
            writeMenu();
            int choice = in.nextInt();
            switch (choice){
                case 1:
                    controller.execute(Messages.ADD_PATIENT);
                    break;
                case 2:
                    controller.execute(Messages.REMOVE_PATIENT);
                    break;
                case 3:
                    controller.execute(Messages.GET_ALL_BLOCKS);
                    break;
                case 4:
                    controller.execute(Messages.END_APP);
                    end = true;
                    break;
            }
            try {
                if(!end) {System.in.read();}
            } catch (IOException e) {throw new RuntimeException(e);}
        }
    }
    public void writeMenu(){
        System.out.println("1 - Vytvorit pacienta");
        System.out.println("2 - Vymazat pacienta");
        System.out.println("3 - Vypis vsetky bloky");
        System.out.println("4 - Koniec");
    }

    public void writeOutput(String number, String text){
        System.out.println("Pocet riadkov: " + number);
        System.out.println("Output: \n" +text);
    }

    @Override
    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void showDialogStructure(){
        System.out.println("Vyber pouzivanu struktutu: ");
        System.out.println("0 - Staticy hesovaci subor");
        System.out.println("1 - Dynamicky hesovaci subor");
        selectedStructure = in.nextInt();
        in.nextLine();
    }

    @Override
    public int getSelectedStructure() {
        return selectedStructure;
    }

    public void showDialogFilename() {
        System.out.println("Zadajte nazov suboru: ");
        filename = in.nextLine();
    }

    @Override
    public String getFileName() {
        return filename;
    }

    @Override
    public void showDialogFileSettings() {}

    public String getPatientName() {
        System.out.println("Meno pacienta: ");
        return in.nextLine();
    }

    public String getPatientSurname() {
        System.out.println("Priezvisko pacienta: ");
        return in.nextLine();
    }

    public String getPatientIc() {
        System.out.println("Rodne cislo pacienta: ");
        return in.nextLine();
    }

    public String getBirthDate() {
        System.out.println("Datum narodenia pacienta: ");
        return in.nextLine();
    }

    public String getIc() {
        System.out.println("Kod zdravotnej poistovne pacienta: ");
        return in.nextLine();
    }

    public String getBlockFactorSet() {
        System.out.println("Blokovaci faktor: ");
        return in.nextLine();
    }

    public String getQuantityOfRecordsSet() {
        System.out.println("Pocet blokov: ");
        return in.nextLine();
    }

    @Override
    public String getUserInput(UserInputType userInput) {
        switch (userInput){
            case PATIENT_NAME:
                return getPatientName();
            case PATIENT_SURNAME:
                return getPatientSurname();
            case PATIENT_ID:
                return getPatientIc();
            case PATIENT_IC:
                return getIc();
            case PATIENT_BIRTH:
                return getBirthDate();
            case HOSPITALIZATION_ID:
                return getHospiId();
            case DIAGNOSIS:
                return getDiagnosis();
            case HOSPITALIZATION_END:
                return getHospiEnd();
            case HOSPITALIZATION_START:
                return getHospiStart();
            case DB_QUANTITY_PATIENTS:
                return getDBQuaPat();
            case DB_QUANTITY_HOSPITALIZATIONS:
                return getDBQuaHospi();
        }
        return null;
    }

    private String getDBQuaHospi() {
        System.out.println("Pocet hospitalizacii: ");
        return in.nextLine();
    }

    private String getDBQuaPat() {
        System.out.println("Pocet pacientov: ");
        return in.nextLine();
    }

    private String getHospiStart() {
        System.out.println("Zaciatok hospitalizacie: ");
        return in.nextLine();
    }

    private String getHospiEnd() {
        System.out.println("Koniec hospitalizacie: ");
        return in.nextLine();
    }

    private String getDiagnosis() {
        System.out.println("Diagnoza: ");
        return in.nextLine();
    }

    private String getHospiId() {
        System.out.println("ID hospitalizacie: ");
        return in.nextLine();
    }
}
