package other;
import data.Hospitalization;
import data.Patient;
import structure.Block;
import structure.DynamicHashing;
import structure.Hashing;
import structure.StaticHashing;
import structure.factories.StructureFactory;
import structure.memento.*;

import java.time.LocalDate;
import java.util.ArrayList;

public class App_core {

    Hashing<Patient> hashing;
    Generator<Patient> gen = new Generator<>();
    History hashingHistory = new History();
    public App_core() { }
    public String addHospitalization(String dateFrom, String dateTo, String diagnosis, String patientId){
        if (dateFrom.equals("") || diagnosis.equals("") || patientId.equals("")) {return "Chybajuci udaj";}
        Patient dummyPat = new Patient("x", "x", patientId, LocalDate.parse("1900-01-01"), 5);
        Block<Patient> block = hashing.getBlock(dummyPat);
        Patient pat = block.getData(dummyPat);
        if (pat == null) {return "Pacient sa nenasiel!";}
        boolean ok = false;
        Hospitalization hospi = new Hospitalization(pat.getNewHospiId(), LocalDate.parse(dateFrom), diagnosis);
        if (!dateTo.equals("")) {
            hospi.setEndOfHospitalization(LocalDate.parse(dateTo));
        }
        if (pat.addHospitalization(hospi)) {
            //------------------------NV-------------------------//
            hashingHistory.push(new Memento(CommandReverser.getInstance(), new AddHospiCommand(hashing, pat, hospi)));

            hashing.updateBlock(block, pat);
            return "Hospitalizacia uspesne pridana";
        }
        return "Hospitalizacia nepridana";
    }

    public String endHospitalization(String dateTo, String patientId) {
        if (dateTo.equals("") || patientId.equals("")) {return "Chybajuci udaj";}
        Patient dummyPat = new Patient("x", "x", patientId, LocalDate.parse("1900-01-01"), 5);
        Block<Patient> block = hashing.getBlock(dummyPat);
        Patient pat = block.getData(dummyPat);
        if (pat == null) {return "Pacient sa nenasiel!";}
        Hospitalization hospi = pat.getEndHospiNull();
        if (hospi == null) {return "Pacient nema ziadnu neukoncenu hospitalizaciu!";}
        //------------------------NV-------------------------//
        hashingHistory.push(new Memento(CommandReverser.getInstance(), new SetEndHospiToCommand(hashing, pat, hospi, LocalDate.parse(dateTo))));

        hospi.setEndOfHospitalization(LocalDate.parse(dateTo));
        hashing.updateBlock(block, pat);
        return "Hospitalizacia uspesne zmenena";
    }

    public String addPatient(String name, String surname, String id, String birthDate, String ic_code) {
        if (name.equals("") || surname.equals("") || id.equals("") || birthDate.equals("") || ic_code.equals("")) {return "Chybajuci udaj";}
        Patient pat = new Patient(name, surname, id, LocalDate.parse(birthDate), Integer.parseInt(ic_code));
        boolean success = hashing.insert(pat);
        //------------------------NV-------------------------//
        if (success) {hashingHistory.push(new Memento(CommandReverser.getInstance(), new InsertCommand(hashing, pat)));}

        return success ? "Pacient uspesne pridany" : "Pacient nepridany";
    }

    public String generateData(String numOfPatients, String numOfHospitalizations) {
        if (numOfPatients.equals("") || numOfHospitalizations.equals("")) {return "Chybajuci udaj";}
        ArrayList<Patient> patients =  gen.generatePatients(Integer.parseInt(numOfPatients));
        gen.generateHospitalizations(Integer.parseInt(numOfHospitalizations) ,patients);
        for (Patient pat : patients) {
            hashing.insert(pat);
        }
        return "Databaza naplnena";
    }

    public String getPatient(String id) {
        if (id.equals("")) {return "Chybajuci udaj";}
        Patient pat = hashing.find(new Patient("x", "x", id, LocalDate.parse("1900-01-01"), 5));
        if (pat == null) {return "Pacient sa nenasiel!";}
        return pat.print();
    }

    public String removePatient(String id) {
        if (id.equals("")) {return "Chybajuci udaj";}
        Patient pat = new Patient("x", "x", id, LocalDate.parse("1900-01-01"), 5);
        //------------------------NV-------------------------//
        hashingHistory.push(new Memento(CommandReverser.getInstance(), new DeleteCommand(hashing, hashing.find(pat))));

        boolean success = hashing.delete(pat);

        //------------------------NV-------------------------//
        if (!success) {hashingHistory.pop();}

        return success ? "Pacient uspesne odstraneny" : "Pacient neodstraneny";
    }

    public String getHospi(String idPatient, String idHospi) {
        if (idPatient.equals("") || idHospi.equals("")) {return "Chybajuci udaj";}
        Patient pat = hashing.find(new Patient("x", "x", idPatient, LocalDate.parse("1900-01-01"), 5));
        if (pat == null) {return "Pacient sa nenasiel!";}
        Hospitalization hospi = pat.getHospitalization(new Hospitalization(Byte.parseByte(idHospi), LocalDate.parse("1900-01-01"), LocalDate.parse("1900-01-01"), "x"));
        if (hospi == null) {return "Hospitalizacia sa nenasla!";}
        return hospi.print();
    }

    public String deleteHospi(String idPatient, String idHospi) {
        if (idPatient.equals("") || idHospi.equals("")) {return "Chybajuci udaj";}
        Patient dummyPat = new Patient("x", "x", idPatient, LocalDate.parse("1900-01-01"), 5);
        Block<Patient> block = hashing.getBlock(dummyPat);
        Patient pat = block.getData(dummyPat);
        if (pat == null) {return "Pacient sa nenasiel!";}
        Hospitalization hospi = new Hospitalization(Byte.parseByte(idHospi), LocalDate.parse("1900-01-01"), LocalDate.parse("1900-01-01"), "x");
        if (!pat.deleteHospitalization(hospi)) {
            return "Hospitalizacia nebola odstranena!";
        }
        //------------------------NV-------------------------//
        hashingHistory.push(new Memento(CommandReverser.getInstance(), new RemoveHospiCommand(hashing, pat, hashing.find(pat).getHospitalization(hospi))));

        hashing.updateBlock(block, pat);
        return "Hospitalizacia uspesne odstranena";
    }

//    public void createDynamicHash(boolean fileExists, int blockFactor, String fileName) {
//        if(fileExists) {
//            this.hashing = new DynamicHashing<>(fileName, Patient.class);
//        } else {
//            this.hashing = new DynamicHashing<>(blockFactor, fileName, Patient.class);
//        }
//    }
//
//    public void createStaticHash(boolean fileExists, int blockFactor, String fileName, int quantityOfRecords) {
//        if(fileExists) {
//            this.hashing = new StaticHashing<>(fileName, Patient.class);
//        } else {
//            this.hashing = new StaticHashing<>(blockFactor, fileName, quantityOfRecords, Patient.class);
//        }
//    }

    public void createStructure(StructureFactory factory, boolean fileExists, int blockFactor, String fileName, int quantityOfRecords) {
        this.hashing = factory.createStructure(fileExists, blockFactor, fileName, quantityOfRecords);
    }
    public void endHashing() {
        if (this.hashing instanceof StaticHashing) {
            this.hashing.endHashing();
        } else if (this.hashing instanceof DynamicHashing) {
            ((DynamicHashing<?>)this.hashing).endDynamicHashing();
        }
    }

    public String getBlocks() {
        return hashing.printAllBlocks();
    }

    public String undoOperation() {
        if (hashingHistory.undo()) {
            return "Undo successful";
        } else {
            return "Undo unsuccessful";
        }
    }
}
