import java.util.*;

public class GifflerThompson {
    static ArrayList<Truck> trucks = Truck.generateTrucksFromCSV("tan_kaucuk_data.csv");
    static ArrayList<Truck> orderedList = new ArrayList<>();
    static ArrayList<Truck> dockedOrderedList = new ArrayList<>();
    static ArrayList<Truck> notDockedOrderedList = new ArrayList<>();
    static HashMap<Truck, Double> criticalRatioList = new HashMap<>();
    static boolean dockedAvailable;
    static boolean notDockedAvailable;
    static int availableWorkers  = 5;
    static int day = 0;
    static int minute = 0;
    static int negativeCounter = 0;

    public static void main(String[] args) {
        calculateQueues();
        processWeek(orderedList);
    }

    public static void calculateQueues() {
        for(Truck t: trucks) {
            criticalRatioList.put(t, t.getCriticalRatio());
            orderedList.add(t);
        }

        for (int i = 0; i < trucks.size(); i++) {
            Truck addingTruck = findNextBestRatioAndRemove(criticalRatioList);

            if(addingTruck.getCriticalRatio()>=0) {
                orderedList.set(i,addingTruck);
            } else {
                orderedList.set(orderedList.size()-1-negativeCounter,addingTruck);
                negativeCounter++;
            }
        }

        for (int i = 0; i < orderedList.size()-1; i++) { //0'dan 18'e
            if (orderedList.get(i).getCriticalRatio() == orderedList.get(i + 1).getCriticalRatio()) {
                if (orderedList.get(i).getPriority() < orderedList.get(i + 1).getPriority()) {
                    Truck temp = null;
                    temp = orderedList.get(i);
                    orderedList.set(i, orderedList.get(i + 1));
                    orderedList.set(i + 1, temp);
                }
            }
        }

        for (Truck t : orderedList) {
            if (t.getIsDocked()) {
                dockedOrderedList.add(t);
            } else {
                notDockedOrderedList.add(t);
            }
        }

        System.out.println("List of docked trucks ordered according to critical ratio and priority: ");
        System.out.println(" ");

        for (int i = 0; i < dockedOrderedList.size(); i++) {
            System.out.println("Order: " + (i + 1));
            System.out.println("Truck ID: " + dockedOrderedList.get(i).getId());
            System.out.println("Critical ratio: " + String.format("%.2f", dockedOrderedList.get(i).getCriticalRatio()));
            System.out.println("Priority: " + dockedOrderedList.get(i).getPriority());
            System.out.println("Process time: " + dockedOrderedList.get(i).getProcessTime());
            System.out.println("Number of workers needed: " + dockedOrderedList.get(i).getNeededWorkers());
            System.out.println("Is it a foreign truck? : " + dockedOrderedList.get(i).getIsForeign());
            System.out.println("---------------------------------------");
        }

        System.out.println(" ");
        System.out.println("List of not docked trucks ordered according to critical ratio and priority: ");
        System.out.println(" ");

        for (int i = 0; i < notDockedOrderedList.size(); i++) {
            System.out.println("Order: " + (i + 1));
            System.out.println("Truck ID: " + notDockedOrderedList.get(i).getId());
            System.out.println("Critical ratio: " + String.format("%.2f", notDockedOrderedList.get(i).getCriticalRatio()));
            System.out.println("Priority: " + notDockedOrderedList.get(i).getPriority());
            System.out.println("Process time: " + notDockedOrderedList.get(i).getProcessTime());
            System.out.println("Number of workers needed: " + notDockedOrderedList.get(i).getNeededWorkers());
            System.out.println("Is it a foreign truck? : " + notDockedOrderedList.get(i).getIsForeign());
            System.out.println("---------------------------------------");
        }

    }
    // en büyük ratio değil en küçük ratio önce olacak ama negatif olanlar sıranın sonunda olacak (negatifler arasında büyük-küçük fark ediyor mu random mu atıyor karar vermek lazım)
    private static Truck findNextBestRatioAndRemove(HashMap<Truck, Double> criticalRatioList) {
        Truck best = null;
        double bestRatio = Integer.MAX_VALUE;
        for (Truck s : criticalRatioList.keySet()) {
                if (criticalRatioList.get(s) < bestRatio) {
                    bestRatio = criticalRatioList.get(s);
                    best = s;
                }
        }
        criticalRatioList.remove(best);
        return best;
    }

    public static Truck chooseNextTruckAndRemove(ArrayList<Truck> orderedList, int availableWorkers) {
        Truck nextTruck = null;

                for (Truck t: orderedList) {
                    if (availableWorkers >= t.getNeededWorkers()) {
                        nextTruck = t;
                    }
                }

        orderedList.remove(nextTruck);
        return nextTruck;
    }

    public static void processWeek(ArrayList<Truck> orderedList) {

        dockedAvailable = false;
        notDockedAvailable = false;
        availableWorkers = 5;
        int dockedStartTime = 0;
        int notDockedStartTime = 0;

        for (day = 0; day <= 4; day++) {
            for (minute = 0; minute <= 480; minute++) {
                for(int aTruck = 0; aTruck < orderedList.size(); aTruck++) {

                    Truck nextTruck = chooseNextTruckAndRemove(orderedList,availableWorkers);
                    
                    if (nextTruck.getIsDocked()) {
                        if (dockedAvailable) {
                            dockedStartTime = day*480+minute;
                            do {
                                dockedAvailable = false;
                                availableWorkers = availableWorkers - nextTruck.getNeededWorkers();
                            } while (minute < (dockedStartTime + nextTruck.getProcessTime()));
                        }
                        System.out.println(nextTruck.getId());
                    } else {
                        if (notDockedAvailable) {
                            notDockedStartTime = day*480+minute;
                            do {
                                notDockedAvailable = false;
                                availableWorkers = availableWorkers - nextTruck.getNeededWorkers();
                            } while (minute < (notDockedStartTime + nextTruck.getProcessTime()));
                        }
                        System.out.println(nextTruck.getId());
                    }

                }
            }
        }

    }
}

