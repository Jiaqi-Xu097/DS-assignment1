package Calculator;

import java.rmi.Naming;

public class CalculatorClient implements Runnable {
    private String clientId;
    private Calculator calculator;

    public CalculatorClient(String clientId, Calculator calculator) {
        this.clientId = clientId;
        this.calculator = calculator;
    }

    @Override
    public void run() {
        try {
            // Test pushValue
            calculator.pushValue(clientId, 10);
            calculator.pushValue(clientId, 20);
            calculator.pushValue(clientId, 30);

//            System.out.println("Popped Element: "+ calculator.pop(clientId));

            // Test pushOperation
           int min = calculator.pushOperation(clientId, "min");
          System.out.println("Client " + clientId + " Min: " + min); // Should print 10

            calculator.pushValue(clientId, 40);
            calculator.pushValue(clientId, 50);
            calculator.pushValue(clientId, 60);

            int max = calculator.pushOperation(clientId, "max");
            System.out.println("Client " + clientId + " Max: " + max);

            // Test delayPop
//           calculator.pushValue(clientId, 1);
//            calculator.pushValue(clientId, 2);
//            calculator.pushValue(clientId, 3);
            int poppedValue = calculator.delayPop(clientId, 5000);
            System.out.println("Popped Element: " + poppedValue);

            // Check if stack is empty
           System.out.println("Client " + clientId + " Is stack empty: " + calculator.isEmpty(clientId));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            Calculator calculator = (Calculator) Naming.lookup("rmi://localhost/CalculatorService");

            // Create and start multiple client threads
            int numberOfClients = 5;
            Thread[] clients = new Thread[numberOfClients];

            for (int i = 0; i < numberOfClients; i++) {
                String clientId = "Client" + (i + 1);
                clients[i] = new Thread(new CalculatorClient(clientId, calculator));
                clients[i].start();
            }

            // Wait for all threads to finish
            for (Thread client : clients) {
                client.join();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}