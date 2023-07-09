package data;

import structure.IData;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Random;

public class Patient implements IData<Patient> {
    private String name;
    private String surname;
    private String id;
    private LocalDate birthDate;
    private Integer ic_code;
    private ArrayList<Hospitalization> hospitalizations;
    private int hospisSize;

    private final int dateLength = 10;
    private final int maxNameSize = 15;
    private final int maxSurnameSize = 20;
    private final int maxIdSize = 10;
    private final int maxHospiCount = 10;

    private final int headSize = 4 * Byte.BYTES;

    public Patient(String name, String surname, String id, LocalDate birthDate, int ic_code) {
        hospitalizations = new ArrayList<>(maxHospiCount);
        for (int i = 0; i < this.maxHospiCount; i++) {
            try {
                this.hospitalizations.add(Hospitalization.class.newInstance());//asi netreba createInstance
            } catch (InstantiationException | IllegalAccessException e) {e.printStackTrace();}
        }
        this.name = name;
        this.surname = surname;
        this.id = id;
        this.birthDate = birthDate;
        this.ic_code = ic_code;
    }

    public Patient() {hospitalizations = new ArrayList<>(maxHospiCount);
        for (int i = 0; i < this.maxHospiCount; i++) {
            try {
                this.hospitalizations.add(Hospitalization.class.newInstance());//asi netreba createInstance
            } catch (InstantiationException | IllegalAccessException e) {e.printStackTrace();}
        }
    }

    public boolean addHospitalization(Hospitalization hospitalization) {
        if (hospitalization.getEndOfHospitalization() == null && checkEndHospi()) {return false;}
        if (existsHospi(hospitalization) || hospisSize == maxHospiCount) {return false;}
        this.hospitalizations.set(hospisSize, hospitalization);
        hospisSize++;
        return true;
    }

    public Hospitalization getHospitalization(Hospitalization hospitalization) {
        for (int i = 0; i < hospisSize; i++) {
            if (hospitalizations.get(i).getHospitalizationId() == hospitalization.getHospitalizationId()) {
                return hospitalizations.get(i);
            }
        }
        return null;
    }

    private boolean existsHospi(Hospitalization hospitalization) {
        for (int i = 0; i < hospisSize; i++) {
            if (hospitalizations.get(i).getHospitalizationId() == hospitalization.getHospitalizationId()) {
                return true;
            }
        }
        return false;
    }

    //Zisti ci uz je nejaka hospitalizacia pacienta v nemocnici NULL
    private boolean checkEndHospi() {
        for (int i = 0; i < hospisSize; i++) {
            if (hospitalizations.get(i).getEndOfHospitalization() == null) {
                return true;
            }
        }
        return false;
    }

    public Hospitalization getEndHospiNull(){
        for (int i = 0; i < hospisSize; i++) {
            if (hospitalizations.get(i).getEndOfHospitalization() == null) {
                return hospitalizations.get(i);
            }
        }
        return null;
    }

    public ArrayList<Hospitalization> getHospitalizations() {return hospitalizations;}
    public String getId() {return id;}
    public String getName() {return name;}
    public String getSurname() {return surname;}
    public int getIc_code() {return ic_code;}
    public LocalDate getBirthDate() {return birthDate;}
    public byte getNewHospiId(){
        Random rnd = new Random();
        if (hospisSize == 0) {return Integer.valueOf(rnd.nextInt(100)+1).byteValue();}
        byte newHospiId = Integer.valueOf(rnd.nextInt(100)+1).byteValue();

        ArrayList<Byte> hospitalizationsIds = new ArrayList<>(10);
        for (int i = 0; i < hospisSize; i++) {
            hospitalizationsIds.add(hospitalizations.get(i).getHospitalizationId());
        }

        boolean found = false;
        while (!found) {
            if (hospitalizationsIds.contains(newHospiId)) {
                newHospiId = Integer.valueOf(rnd.nextInt(100)+1).byteValue();
            } else {
                found = true;
            }
        }

        return newHospiId;
    }

    public String print(){
        return String.format("%-30s", name +" "+ surname)  +" "+ id +" "+ birthDate +" "+ String.format("%1$5s", ic_code)+"\n"+printHospitalizations();
    }

    public String printHospitalizations(){
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < hospisSize; i++) {
            output.append("\t").append(hospitalizations.get(i).print()).append("\n");
        }
        return output.toString();
    }

    @Override
    public BitSet getHash() {
        return BitSet.valueOf(new long[]{Long.parseLong(this.id)});
    }

    @Override
    public boolean myEquals(Patient data) {
        return this.id.equals(data.getId());
    }

    @Override
    public byte[] toByteArray() {
        ByteArrayOutputStream btA = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(btA);
        if (name == null) {return notDefinedPatient();}
        try {
            //Header
            out.writeByte(name.length());
            out.writeByte(surname.length());
            out.writeByte(id.length());
            out.writeByte(hospisSize);
            //Data
            //out.writeChars(String.format("%1$"+maxNameSize+ "s",name));
            out.writeChars(String.format("%-" + maxNameSize + "." + maxNameSize + "s", name));
            out.writeChars(String.format("%-" + maxSurnameSize + "." + maxSurnameSize + "s", surname));
            out.writeChars(String.format("%-" + maxIdSize + "." + maxIdSize + "s", id));
            if (birthDate == null){
                out.writeChars(String.format("%1$"+dateLength+ "s","x"));
            } else {
                out.writeChars(birthDate.toString());
            }
            out.writeByte(ic_code.byteValue());
            for (int i = 0; i < maxHospiCount; i++) {
//                if (hospisSize <= i || hospitalizations.get(i) == null) {
//                    out.write(new Hospitalization().toByteArray());
//                } else {
                    out.write(hospitalizations.get(i).toByteArray());
//                }
            }

            return btA.toByteArray();
        } catch (IOException e) {e.printStackTrace();}
        return notDefinedPatient();
    }

    private byte[] notDefinedPatient() {
        return new byte[getSize()];
    }

    @Override
    public void fromByteArray(byte[] bytes) throws IOException {
        DataInputStream inp = new DataInputStream(new ByteArrayInputStream(bytes));
        //Header
        int nameSize = inp.readByte();
        int surnameSize = inp.readByte();
        int idSize = inp.readByte();
        hospisSize = inp.readByte();
        //Data
        clearAllStrings();
        for (int i = 0; i < maxNameSize; i++) {
            char letter = inp.readChar();
            if (i < nameSize) {name += letter;}
        }

        for (int i = 0; i < maxSurnameSize; i++) {
            char letter = inp.readChar();
            if (i < surnameSize) {surname += letter;}
        }

        for (int i = 0; i < maxIdSize; i++) {
            char letter = inp.readChar();
            if (i < idSize) {id += letter;}
        }

        String dateS = "";
        for (int i = 0; i < dateLength; i++) {
            dateS += inp.readChar();
        }
        if (dateS.matches("([0-9]{4})-([0-9]{2})-([0-9]{2})")){
            birthDate = LocalDate.parse(dateS);
        }
        ic_code = Byte.toUnsignedInt(inp.readByte());

        for (int i = 0; i < maxHospiCount; i++) {
            int sizeOfRecord = hospitalizations.get(i).getSize();
            byte[] n = Arrays.copyOfRange(bytes, getSizeWithoutHospis() + (i * sizeOfRecord), getSizeWithoutHospis() + ((i+1)*sizeOfRecord));
            hospitalizations.get(i).fromByteArray(n);
        }
    }

    private void clearAllStrings() {
        name = "";
        surname = "";
        id = "";
    }

    @Override
    public int getSize() {
        return headSize + (Character.BYTES * (maxIdSize + maxSurnameSize + maxNameSize + dateLength)) + Byte.BYTES + (maxHospiCount * new Hospitalization().getSize() );
    }
    public int getSizeWithoutHospis() {
        return headSize + (Character.BYTES * (maxIdSize + maxSurnameSize + maxNameSize + dateLength)) + Byte.BYTES;
    }

    public boolean deleteHospitalization(Hospitalization hospitalization) {
        for (int i = 0; i < hospisSize; i++) {
            if (hospitalizations.get(i).getHospitalizationId() == hospitalization.getHospitalizationId()) {
                hospitalizations.set(i, hospitalizations.get(hospisSize-1));
                hospisSize--;
                return true;
            }
        }
        return false;
    }
}
