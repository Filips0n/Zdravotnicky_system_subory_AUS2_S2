package other.controller;

import other.App_core;
import other.Messages;
import other.UserInputType;
import other.view.UI;
import structure.factories.DynamicHashingFactory;
import structure.factories.StaticHashingFactory;

import java.io.File;

public class Controller {
    protected final UI ui;
    protected final App_core core;

    public Controller(UI ui, App_core core) {
        this.ui = ui;
        this.core = core;
        ui.setController(this);
    }
    public void createSelectedStructure() {
        ui.showDialogStructure();
        int structure = ui.getSelectedStructure();
        ui.showDialogFilename();
        String fileName = ui.getFileName();
        if (structure == 0) {
            if(!new File(fileName).isFile()) {
                ui.showDialogFileSettings();
                //core.createStaticHash(false,Integer.parseInt(ui.getBlockFactorSet()) , fileName, Integer.parseInt(ui.getQuantityOfRecordsSet()));
                core.createStructure(new StaticHashingFactory(), false,Integer.parseInt(ui.getBlockFactorSet()) , fileName, Integer.parseInt(ui.getQuantityOfRecordsSet()));
            } else {
                //core.createStaticHash(true,-1, fileName,-1);
                core.createStructure(new StaticHashingFactory(), true,-1, fileName,-1);
            }
        } else {
            if(!new File(fileName).isFile()) {
                ui.showDialogFileSettings();
                //core.createDynamicHash(false,Integer.parseInt(ui.getBlockFactorSet()), fileName);
                core.createStructure(new DynamicHashingFactory(), false,Integer.parseInt(ui.getBlockFactorSet()), fileName, -1);
            } else {
                //core.createDynamicHash(true,-1, fileName);
                core.createStructure(new DynamicHashingFactory(), true,-1, fileName, -1);
            }
        }
    }

    public void endHashing() {
        this.core.endHashing();
    }

    public void execute(Messages message) {
        switch (message){
            case ADD_PATIENT:
                ui.writeOutput("",core.addPatient(ui.getUserInput(UserInputType.PATIENT_NAME), ui.getUserInput(UserInputType.PATIENT_SURNAME), ui.getUserInput(UserInputType.PATIENT_ID), ui.getUserInput(UserInputType.PATIENT_BIRTH), ui.getUserInput(UserInputType.PATIENT_IC))) ;
                break;
            case REMOVE_PATIENT:
                ui.writeOutput("", core.removePatient(ui.getUserInput(UserInputType.PATIENT_ID)));
                break;
            case UPDATE_PATIENT:
                break;
            case GET_PATIENT:
                ui.writeOutput("", core.getPatient(ui.getUserInput(UserInputType.PATIENT_ID)));
                break;
            case REMOVE_HOSPITALIZATION:
                ui.writeOutput("", core.deleteHospi(ui.getUserInput(UserInputType.PATIENT_ID), ui.getUserInput(UserInputType.HOSPITALIZATION_ID)));
                break;
            case ADD_HOSPITALIZATION:
                ui.writeOutput("",core.addHospitalization(ui.getUserInput(UserInputType.HOSPITALIZATION_START), ui.getUserInput(UserInputType.HOSPITALIZATION_END), ui.getUserInput(UserInputType.DIAGNOSIS), ui.getUserInput(UserInputType.PATIENT_ID)));
                break;
            case UPDATE_HOSPITALIZATION:
                break;
            case END_HOSPITALIZATION:
                ui.writeOutput("",core.endHospitalization(ui.getUserInput(UserInputType.HOSPITALIZATION_END), ui.getUserInput(UserInputType.PATIENT_ID)));
                break;
            case GET_HOSPITALIZATION:
                ui.writeOutput("", core.getHospi(ui.getUserInput(UserInputType.PATIENT_ID), ui.getUserInput(UserInputType.HOSPITALIZATION_ID)));
                break;
            case GET_ALL_BLOCKS:
                ui.writeOutput("", core.getBlocks());
                break;
            case UNDO_OP:
                ui.writeOutput("", core.undoOperation());
                break;
            case END_APP:
                endHashing();
                break;
            case GENERATE_DATA:
                ui.writeOutput("", core.generateData(ui.getUserInput(UserInputType.DB_QUANTITY_PATIENTS), ui.getUserInput(UserInputType.DB_QUANTITY_HOSPITALIZATIONS)));
                break;
        }
    }
}
