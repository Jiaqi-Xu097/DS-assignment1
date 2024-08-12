package Calculator;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.lang.Thread;

public class CalculatorImplementation extends UnicastRemoteObject implements Calculator {
    private Map<String, Stack<Integer>> clientStacks;

    protected CalculatorImplementation() throws RemoteException {
        clientStacks = new HashMap<>();
    }

    private synchronized Stack<Integer> getClientStack(String clientId) {
        return clientStacks.computeIfAbsent(clientId, k -> new Stack<>());
    }

    @Override
    public synchronized void pushValue(String clientId, int val) throws RemoteException {
        Stack<Integer> stack = getClientStack(clientId);
        System.out.println("Client " + clientId + " requesting to push value: " + val);
        System.out.println("Stack before push: " + stack);

        stack.push(val);

        System.out.println("Client " + clientId + " pushed value: " + val);
        System.out.println("Stack after push: " + stack);
    }

    @Override
    public int pushOperation(String clientId, String operator) throws RemoteException {
        Stack<Integer> stack = getClientStack(clientId);
        System.out.println("Client " + clientId + " performing operation: " + operator + " | Stack before operation: " + stack);

        if (stack.isEmpty()) {
            throw new RemoteException("Stack is empty");
        }

        int result;
        switch (operator) {
            case "min":
                result = stack.stream().min(Integer::compare).orElseThrow();
                stack.clear();
                stack.push(result);
                break;
            case "max":
                result = stack.stream().max(Integer::compare).orElseThrow();
                stack.clear();
                stack.push(result);
                break;
            case "lcm":
                result = stack.stream().reduce(1, (a, b) -> lcm(a, b));
                stack.clear();
                stack.push(result);
                break;
            case "gcd":
                result = stack.stream().reduce(stack.pop(), (a, b) -> gcd(a, b));
                stack.clear();
                stack.push(result);
                break;
            default:
                throw new RemoteException("Invalid operator");
        }

        System.out.println("Client " + clientId + " performed operation: " + operator + " | Stack after operation: " + stack);
        return result;
    }
    private int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }

    private int lcm(int a, int b) {
        return a * (b / gcd(a, b));
    }

    @Override
    public synchronized int pop(String clientId) throws RemoteException {
        Stack<Integer> stack = getClientStack(clientId);
        if (stack.isEmpty()) {
            throw new RemoteException("Stack is empty");
        }
        int value = stack.pop();
        System.out.println("Client " + clientId + " popped value: " + value);
        System.out.println("Stack after pop: " + stack);
        return value;
    }

    @Override
    public synchronized boolean isEmpty(String clientId) throws RemoteException {
        return getClientStack(clientId).isEmpty();
    }

    @Override
    public synchronized int delayPop(String clientId, int millis) throws RemoteException {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RemoteException("Interrupted", e);
        }
        return pop(clientId);
    }

    // The pushOperation method remains the same or add similar debug statements.
}
