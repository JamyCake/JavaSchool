package com.tsystems.javaschool.tasks.calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class Calculator {
    /**
     * Evaluate statement represented as string.
     *
     * @param statement mathematical statement containing digits, '.' (dot) as decimal mark,
     *                  parentheses, operations signs '+', '-', '*', '/'<br>
     *                  Example: <code>(1 + 38) * 4.5 - 1 / 2.</code>
     * @return string value containing result of evaluation or null if statement is invalid
     */

    private static final char OPENING_BRACKET = '(';
    private static final char CLOSING_BRACKET = ')';

    private Stack<Double> numbers;
    private Stack<Character> operators;

    private Map<Character, Integer> operatorsPriority;

    private char [] buff;

    public String evaluate(String statement) {
        char [] chars = getChars(statement);
        if (chars == null) return null;

        initFields();

        try {
            simplifyExpression(chars);
            calculateExpression();
        } catch (Exception e){
            return null;
        }

        return formatResult();
    }




    //getChars Block
    private char[] getChars(String statement) {
        if (statement == null) return null;
        String expression = removeSpaces(statement);
        if (expressionIsNotCorrect(expression)) return null;
        char [] chars = expression.toCharArray();
        if (bracketsCountIsSame(chars)){
            return chars;
        } else {
            return null;
        }
    }

    private String removeSpaces(String statement) {
        return statement.replaceAll(" ", "");
    }

    private boolean expressionIsNotCorrect(String expression) {
        return expression.contains(",") || expression.contains("..") || expression.isEmpty();
    }

    private boolean bracketsCountIsSame(char [] chars){
        int opening = 0;
        int closing = 0;
        for (char currentChar : chars){
            if (currentChar == OPENING_BRACKET){ ++opening; }
            if (currentChar == CLOSING_BRACKET){ ++closing; }
        }
        return opening == closing;
    }




    //initField Block
    private void initFields(){
        numbers = new Stack<>();
        operators = new Stack<>();

        operatorsPriority = new HashMap<>();
        setOperatorsPriority();

        buff = new char[64];
    }

    private void setOperatorsPriority() {
        operatorsPriority.put('+', 1);
        operatorsPriority.put('-', 1);
        operatorsPriority.put('*', 2);
        operatorsPriority.put('/', 2);
        operatorsPriority.put('(', 3);
        operatorsPriority.put(')', 0);
    }




    //simplifyExpression Block
    private void simplifyExpression(char [] chars)
            throws NumberFormatException, EmptyStackException, ArithmeticException {

        int buffCharIndex = 0;
        for (char currentChar : chars){

            if (currentCharIsOperator(currentChar)) {
                if (currentChar == OPENING_BRACKET){
                    operators.push(currentChar);
                    continue;
                }

                pushNumber(buff);

                if (!operators.empty()) {
                    char previousOperator = operators.pop();
                    if (needToHoldThePreviousOperator(currentChar, previousOperator)){
                        operators.push(previousOperator);
                    } else {
                        calculateTwoTopNumbers(previousOperator);
                    }
                }

                if (currentChar == CLOSING_BRACKET){
                    operators.pop();
                } else {
                    operators.push(currentChar);
                }

                clearBuffer();
                buffCharIndex = 0;
            } else {
                buff[buffCharIndex] = currentChar;
                ++buffCharIndex;
            }
        }
        pushNumber(buff);
    }

    private boolean currentCharIsOperator(char c){
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '(' || c == ')';
    }

    private void pushNumber(char[] buff) {
        try {numbers.push(getNumber(buff)); } catch (NumberFormatException ignored){}
    }

    private double getNumber(char[] buff) throws NumberFormatException {
        String numberStr = new String(buff);
        return Double.parseDouble(numberStr);
    }

    private boolean needToHoldThePreviousOperator(char currentChar, char previousOperator){
        if (previousOperator == OPENING_BRACKET) {
            return true;
        }
        return priorityIsHigher(currentChar, previousOperator);
    }

    private boolean priorityIsHigher(char currentOperator, char previousOperator){
        int prior_1 = operatorsPriority.get(currentOperator);
        int prior_2 = operatorsPriority.get(previousOperator);
        return prior_1 > prior_2;
    }

    private void clearBuffer() {
        Arrays.fill(buff, '\0');
    }




    //calculateExpression Block
    private void calculateExpression() {
        while (numbers.size() != 1) {
            calculateTwoTopNumbers(operators.pop());
        }
    }

    private void calculateTwoTopNumbers(char previousOperator) throws EmptyStackException {
        double top = numbers.pop();
        double down = numbers.pop();
        numbers.push(calculate(down, top, previousOperator));
    }

    private double calculate(double leftOperand, double rightOperand, char operator) throws ArithmeticException{
        if (operator == '/' && rightOperand == 0) {throw new ArithmeticException();}
        switch (operator){
            case '+': return leftOperand + rightOperand;
            case '-': return leftOperand - rightOperand;
            case '*': return leftOperand * rightOperand;
            case '/': return leftOperand / rightOperand;
        }
        throw new ArithmeticException();
    }




    //formatResult Block
    private String formatResult() {
        double res = BigDecimal.valueOf(numbers.pop()).setScale(4, RoundingMode.UP).doubleValue();
        if (res % 1 == 0) return String.valueOf((int) res);
        return String.valueOf(res);
    }
}
