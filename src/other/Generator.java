package other;

import data.Hospitalization;
import data.Patient;
import structure.DynamicHashing;
import structure.Hashing;
import structure.IData;
import structure.StaticHashing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

public class Generator<T extends IData<T>> {

    private String getRandomString(String name, File f) {
        Random rnd = new Random();
        int n = 0;
        try {
            for(Scanner sc = new Scanner(f); sc.hasNext(); ) {
                n++;
                String readLine = sc.nextLine();
                if(rnd.nextInt(n) == 0)
                    name = readLine;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return name;
    }

    public String generateName(){
        String name = "";
        File f = new File("names.txt");
        return getRandomString(name, f);
    }

    public String generateDiagnosis(){
        String name = "";
        File f = new File("diagnosis.txt");
        return getRandomString(name, f);
    }

    public LocalDate generateDate(){
        Random rnd = new Random();
        int year = (rnd.nextInt(2022 - 1920) + 1920);
        int month = (rnd.nextInt(12 - 1) + 1);
        String monthS = String.format("%02d", month);
        int day = (rnd.nextInt((YearMonth.from(LocalDate.parse(year+"-"+monthS+"-01")).atEndOfMonth().getDayOfMonth()) - 1) + 1);

        return LocalDate.of(year, month, day);
    }

    public LocalDate generateHospiDate(LocalDate minDate){
        Random rnd = new Random();
        int year = (rnd.nextInt(2022 - minDate.getYear()) + minDate.getYear());
        int month = (rnd.nextInt(12 - 1) + 1);
        if (year == minDate.getYear()) {
            month = (rnd.nextInt(12 - minDate.getMonth().getValue()) + minDate.getMonth().getValue());
        }
        String monthS = String.format("%02d", month);
        int day = (rnd.nextInt((YearMonth.from(LocalDate.parse(year+"-"+monthS+"-01")).atEndOfMonth().getDayOfMonth()) - 1) + 1);
        if (year == minDate.getYear() && month == minDate.getMonth().getValue()) {
            day = (rnd.nextInt((YearMonth.from(LocalDate.parse(year+"-"+monthS+"-01")).atEndOfMonth().getDayOfMonth()) - minDate.getDayOfMonth()) + minDate.getDayOfMonth());
        }
        return LocalDate.of(year, month, day);
    }

    public ArrayList<Patient> generatePatients(int quantity){
        Random rnd = new Random();
        ArrayList<Patient> dataList = new ArrayList<>(quantity);

        String id;
        int ic;
        for (int i = 0; i < quantity; i++){
            id = String.format("%010d", i);
            ic = rnd.nextInt(256);
            dataList.add(
                    new Patient(generateName(), generateName(), id, generateDate(), ic)
            );
        }
        return dataList;
    }

    public ArrayList<Hospitalization> generateHospitalizations(int quantity, ArrayList<Patient> patients){
        ArrayList<Hospitalization> dataList = new ArrayList<Hospitalization>(quantity);
        Random rnd = new Random();

        for (int i = 0; i < patients.size(); i++){
            for (int j = 0; j < quantity/patients.size(); j++) {
                int durationDays = rnd.nextInt(20);
                Patient patient = (Patient) patients.get(i);
                LocalDate date = generateHospiDate(patient.getBirthDate());
                LocalDate dateTo = durationDays > 0 ? date.plusDays(durationDays) : null;
                Hospitalization hospi = new Hospitalization(Integer.valueOf(patient.getNewHospiId()).byteValue(), date, dateTo, generateDiagnosis());
                if (!patient.addHospitalization(hospi)) {
                    continue;
                };
                dataList.add(hospi);
            }
        }
        return dataList;
    }

    public void operationGenerator(Hashing<T> hashFile, int numOfOperations){
        Random rnd = new Random();
        //rnd.setSeed(-6519759326263640190L);
        ArrayList<String> listOfKeys = new ArrayList<>(numOfOperations);
        int operation;
        for(int i = 0; i < numOfOperations; i++){
            operation = rnd.nextInt(8);
            String id = (rnd.nextInt(999999 - 100000) + 100000)+""+(rnd.nextInt(9999 - 1000) + 1000);
            Patient pat = new Patient("aaa", "bbb", id, generateDate(), 5);

            if(operation < 5){
                System.out.printf("I");
                if (hashFile.insert((T) pat)){
                    listOfKeys.add(pat.getId());
                }
            } else if(operation < 6){
                System.out.printf("F");
                hashFile.find((T) pat);
            } else {
                System.out.printf("D");
                if (listOfKeys.size() == 0) {continue;}
                int index = rnd.nextInt(listOfKeys.size());
                if(hashFile.delete((T)new Patient("aaa", "bbb", listOfKeys.get(index), generateDate(), 5))){
                    listOfKeys.remove(index);
                }
            }
            //System.out.print(listOfKeys.size() + "-"+ hashFile.listAllIds().size() + " ");
        }
        //ziskanie vsetkych klucov //Kontrola je velmi zdlhava
        ArrayList<String> allIds = hashFile.listAllIds();
        //Sort
        Collections.sort(listOfKeys);
        Collections.sort(allIds);
        //kontrola zhodnosti klucov
        if (allIds.size() != listOfKeys.size()){
            System.out.println("Nezhodujuce sa velkosti");
        }
        for (int j = 0; j < allIds.size(); j++) {
            if (!listOfKeys.get(j).equals(allIds.get(j))){
                System.out.println("Kluc neexistuje");
            }
        }
    }

    public void testHashing(StaticHashing<T> statHash, DynamicHashing<T> dynHash, int numOfOperations) {
        PrintWriter writerStat;
        PrintWriter writerDyn;
        try {
            writerStat = new PrintWriter("testerStat.csv", "UTF-8");
            writerDyn = new PrintWriter("testerDyn.csv", "UTF-8");
        } catch (FileNotFoundException | UnsupportedEncodingException e) {throw new RuntimeException(e);}
        Random rnd = new Random();
        ArrayList<String> listOfKeys = new ArrayList<>(numOfOperations);
        int operation;
        for(int i = 0; i < numOfOperations; i++){
            operation = rnd.nextInt(10);
            String id = (rnd.nextInt(999999 - 100000) + 100000)+""+(rnd.nextInt(9999 - 1000) + 1000);
            Patient pat = new Patient("aaa", "bbb", id, generateDate(), 5);

            long startTime;
            long elapsedTime;
            if(operation < 5){
                startTime = System.nanoTime();
                boolean okStat = statHash.insert((T) pat);
                elapsedTime = System.nanoTime() - startTime;
                writeToCSV(writerStat, elapsedTime, "i", listOfKeys.size());

                startTime = System.nanoTime();
                boolean okDyn = dynHash.insert((T) pat);
                elapsedTime = System.nanoTime() - startTime;
                writeToCSV(writerDyn, elapsedTime, "i", listOfKeys.size());

                if (okStat && okDyn) {
                    listOfKeys.add(pat.getId());
                }
            } else if(operation < 7){
                startTime = System.nanoTime();
                statHash.find((T) pat);
                elapsedTime = System.nanoTime() - startTime;
                writeToCSV(writerStat, elapsedTime, "f", listOfKeys.size());

                startTime = System.nanoTime();
                dynHash.find((T) pat);
                elapsedTime = System.nanoTime() - startTime;
                writeToCSV(writerDyn, elapsedTime, "f", listOfKeys.size());
            } else {
                if (listOfKeys.size() == 0) {continue;}
                int index = rnd.nextInt(listOfKeys.size());
                T patDelete = (T)new Patient("aaa", "bbb", listOfKeys.get(index), generateDate(), 5);

                startTime = System.nanoTime();
                boolean okStat = statHash.delete((T) patDelete);
                elapsedTime = System.nanoTime() - startTime;
                writeToCSV(writerStat, elapsedTime, "d", listOfKeys.size());

                startTime = System.nanoTime();
                boolean okDyn = dynHash.delete((T) patDelete);
                elapsedTime = System.nanoTime() - startTime;
                writeToCSV(writerDyn, elapsedTime, "d", listOfKeys.size());

                if (okStat && okDyn) {
                    listOfKeys.remove(pat.getId());
                }
            }
        }
        writerStat.close();
        writerDyn.close();
    }

    private void writeToCSV(PrintWriter writer, long elapsedTime, String operation, int quantityOfRecords) {
        writer.println(elapsedTime+";"+operation+";"+quantityOfRecords);
    }
}
