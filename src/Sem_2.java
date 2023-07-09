import other.App_core;
import other.controller.Controller;
import other.view.Cli;
import other.view.Gui;
import other.view.UI;

public class Sem_2 {
    public static void main(String[] args) {
        App_core core = new App_core();
        UI ui = new Gui();
        Controller controller = new Controller(ui, core);
        controller.createSelectedStructure();
        //Musi byt na konci Mainu kvoli CLI
        ui.initView();

        //TEST
//        long startTime;
//        long elapsedTime;
//        Generator<Patient> gen = new Generator<>();
//        for (int k = 0; k < 20; k++) {
//            System.out.println(k);
//            System.out.println("Creating file");
//
////            startTime = System.nanoTime();
////            StaticHashing<Patient> statHash = new StaticHashing<Patient>(10, "testStat.dat",100000);
////            elapsedTime = System.nanoTime() - startTime;
////            System.out.println("Inicializacny cas staticky: " + elapsedTime);
//
////            startTime = System.nanoTime();
//            DynamicHashing<Patient> dynHash = new DynamicHashing<Patient>(3, "testDyn.dat");
////            elapsedTime = System.nanoTime() - startTime;
////            System.out.println("Inicializacny cas dynamicky: " + elapsedTime);
//
//            System.out.println("Testing");
//
//            gen.operationGenerator(dynHash, 10000);
////            gen.testHashing(statHash, dynHash, 100000);
//            //System.out.println(testHash.printAllBlocks());;
//
////            System.out.println("Velkost suboru staticky: " + statHash.fileSize());
////            System.out.println("Velkost suboru dynamicky: " + dynHash.fileSize());
//
//            System.out.println("Deleting file");
//            dynHash.fileDelete();
////            statHash.fileDelete();
//        }
//        System.out.println("Testing complete");
    }
}