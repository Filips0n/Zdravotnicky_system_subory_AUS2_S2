package data;

import structure.IData;

import java.io.*;
import java.time.LocalDate;
import java.util.BitSet;

public class Hospitalization implements IData<Hospitalization> {
    private byte hospitalizationId;
    private LocalDate startOfHospitalization;
    private LocalDate endOfHospitalization;
    private String diagnosis;

    private final int dateLength = 10;
    private final int maxDiagnosisSize = 20;
    private final int headSize = Byte.BYTES;

    public Hospitalization(byte hospitalizationId, LocalDate startOfHospitalization, LocalDate endOfHospitalization, String diagnosis) {
        this.startOfHospitalization = startOfHospitalization;
        this.endOfHospitalization = endOfHospitalization;
        this.diagnosis = diagnosis;
        this.hospitalizationId = hospitalizationId;
    }

    public Hospitalization(byte hospitalizationId, LocalDate startOfHospitalization, String diagnosis) {
        this.startOfHospitalization = startOfHospitalization;
        this.hospitalizationId = hospitalizationId;
        this.diagnosis = diagnosis;
    }

    public Hospitalization(){}

    public String getId(){return ""+hospitalizationId;}
    public byte getHospitalizationId() {return hospitalizationId;}
    public LocalDate getStartOfHospitalization() {return startOfHospitalization;}
    public LocalDate getEndOfHospitalization() {
        return endOfHospitalization;
    }
    public String getDiagnosis() {return diagnosis;}

    public void setEndOfHospitalization(LocalDate endOfHospitalization) {
        this.endOfHospitalization = endOfHospitalization;
    }

    public String print(){
        return hospitalizationId+" "+startOfHospitalization +" "+ endOfHospitalization +" "+ diagnosis;
    }

    @Override
    public BitSet getHash() {
//        BitSet s = BitSet.valueOf(new long[]{Long.parseLong(getId())%quantityOfBlocks});//test
//        return s;
        return null;
    }

    @Override
    public boolean myEquals(Hospitalization data) {
//        return this.hospitalizationId == (Byte.parseByte(data.getId()));//test
        return false;
    }

    @Override
    public byte[] toByteArray() {
        ByteArrayOutputStream btA = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(btA);
        if (diagnosis == null) {return notDefinedHospitalization();}
        try {
            //Header
            out.writeByte(diagnosis.length());
            //Data
            out.writeByte(hospitalizationId);
            if (startOfHospitalization == null){
                out.writeChars(String.format("%1$"+dateLength+ "s","x"));
            } else {
                out.writeChars(startOfHospitalization.toString());
            }

            if (endOfHospitalization == null){
                out.writeChars(String.format("%1$"+dateLength+ "s","x"));
            } else {
                out.writeChars(endOfHospitalization.toString());
            }

            out.writeChars(String.format("%-" + maxDiagnosisSize + "." + maxDiagnosisSize + "s", diagnosis));
            return btA.toByteArray();
        } catch (IOException e) {e.printStackTrace();}
        return notDefinedHospitalization();
    }

    private byte[] notDefinedHospitalization() {
        return new byte[getSize()];
    }

    @Override
    public void fromByteArray(byte[] bytes) throws IOException {
        DataInputStream inp = new DataInputStream(new ByteArrayInputStream(bytes));
        //Header
        int diagnosisSize = inp.readByte();
        //Data
        hospitalizationId = inp.readByte();
        String dateS = "";
        for (int i = 0; i < dateLength; i++) {
            dateS += inp.readChar();
        }
        if (dateS.matches("([0-9]{4})-([0-9]{2})-([0-9]{2})")){
            startOfHospitalization = LocalDate.parse(dateS);
        }

        String dateE = "";
        for (int i = 0; i < dateLength; i++) {
            dateE += inp.readChar();
        }
        if (dateE.matches("([0-9]{4})-([0-9]{2})-([0-9]{2})")){
            endOfHospitalization = LocalDate.parse(dateE);
        }

        diagnosis = "";
        for (int i = 0; i < maxDiagnosisSize; i++) {
            char letter = inp.readChar();
            if (i < diagnosisSize) {diagnosis += letter;}
        }
    }

    @Override
    public int getSize() {
        return headSize + Byte.BYTES + (Character.BYTES * (maxDiagnosisSize + dateLength + dateLength));
    }
}
