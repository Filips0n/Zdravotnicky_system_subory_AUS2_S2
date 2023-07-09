package other.view;

import other.UserInputType;
import other.controller.Controller;

public interface UI {

    public void initView();
    public String getUserInput(UserInputType userInput);

    void writeOutput(String number, String text);

    public void setController(Controller controller);

    void showDialogStructure();

    int getSelectedStructure();

    void showDialogFilename();

    String getFileName();

    void showDialogFileSettings();

    String getBlockFactorSet();

    String getQuantityOfRecordsSet();
}
