package com.daemonize.game;

import com.daemonize.daemonengine.utils.DaemonUtils;
import com.daemonize.daemonengine.utils.Pair;
import com.daemonize.daemonprocessor.annotations.Daemon;
import com.daemonize.daemonprocessor.annotations.SideQuest;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;


@Daemon
public class CommandParser {

    private static String listCmd = "ls";


    private class NonEmptyStack<T> {
        private Stack<T> stack = new Stack();
        private T first;

        public NonEmptyStack(T firstElement) {
            first = firstElement;
        }

        public void push(T element) {
            stack.push(element);
        }

        public T pop() {
            if (!stack.empty())
                return stack.pop();
            else
                return first;
        }

        public T peek() {
            if (stack.empty())
                return first;
            else
                return stack.peek();
        }

        public T bottom() {
            return first;
        }

    }

    private Scanner cmdScanner;
    private NonEmptyStack<Pair<Object, String>> stack;

    public CommandParser(Object reflectiveObject) {
        this.cmdScanner = new Scanner(System.in);
        this.stack = new NonEmptyStack<>(Pair.create(reflectiveObject, "root"));
    }

    @SideQuest
    public void parse() {

        try {

            String cmd = cmdScanner.nextLine();

            if (cmd.equals("fields")) {
                Field[] fieldarray = stack.peek().getFirst().getClass().getDeclaredFields();

                for (Field field : fieldarray) {
                    System.err.println(DaemonUtils.tag() + field.getDeclaringClass().getSimpleName() +  " - Type: " + field.getType() + " - fieldname: " + field.getName());
                }

            } else if (cmd.startsWith("field ")) {

                String fieldName =  cmd.replace("field ", "");
                System.err.println(DaemonUtils.tag() + getField(fieldName).toString());

            } else if (cmd.equals("ret")) {
                stack.pop();
            } else if (cmd.startsWith("get ")) {

                String fieldname = cmd.replace("get ", "");

                Field field = stack.peek().getFirst().getClass().getDeclaredField(fieldname);
                field.setAccessible(true);

                Object o = field.get(stack.peek().getFirst());
                stack.push(Pair.create(o, field.getName()));

            } else if (cmd.startsWith("methods")) {

                Method[] methods = stack.peek().getFirst().getClass().getMethods();

                for (Method method : methods) {
                    System.err.println(DaemonUtils.tag() + stack.peek().getFirst().getClass().getSimpleName() + "->" + method.getName());
                }

            } else if (cmd.startsWith("method ")) {

                String methodName = cmd.replace("method ", "");

                Method[] methods = stack.peek().getFirst().getClass().getMethods();

                for (Method method : methods) {
                    if (method.getName().equals(methodName)) {

                        Class<?>[] params =  method.getParameterTypes();

                        System.err.println(DaemonUtils.tag() + stack.peek().getFirst().getClass().getSimpleName() + "/"
                                + stack.peek().getFirst().getClass().getSimpleName() + "-> "
                                + method.getReturnType().getSimpleName() + " " + method.getName());

                        for(Class<?> param : params) {
                            System.err.println("    Parameter:" + param.getSimpleName());
                        }
                    }
                }


            } else if(cmd.startsWith("toString")) {
                System.err.println(stack.peek().getFirst().toString());
            } else if (isMethod(cmd)) {
                Object ret = execStatement(cmd);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.err.print(DaemonUtils.tag() + stack.peek().getFirst().getClass().getSimpleName() + "(" + stack.peek().getSecond() + ")>");
    }

    private Object getField(String fieldName) throws NoSuchFieldException, IllegalAccessException, InstantiationException {

        Object root;

        if(fieldName.equals("this")) {
            return stack.peek().getFirst();
        }

        if (fieldName.contains(".")) {

            if (fieldName.startsWith(".")) {
                root = stack.bottom().getFirst();
            } else {
                root = stack.peek().getFirst();
            }

            Object currentObject = root;

            if (fieldName.lastIndexOf(".") > 0) {


                fieldName = fieldName.replaceFirst("." , "");

                String[] path = fieldName.split("\\.");
                int cnt = 0;

                while (cnt < path.length) {
                    Field currentFiled = currentObject.getClass().getDeclaredField(path[cnt]);
                    currentFiled.setAccessible(true);
                    currentObject = currentFiled.get(currentObject);
                    cnt++;
                }

            } else {

                Field retField = root.getClass().getDeclaredField(fieldName.replace(".", ""));
                retField.setAccessible(true);

                if (retField.getType().isPrimitive()) {

                    if (retField.getType().equals(int.class)){
                        return retField.get(Integer.valueOf(0));
                    } else if (retField.getType().equals(float.class)) {
                        return retField.get(Float.valueOf(0));
                    } else if (retField.getType().equals(byte.class)) {
                        return retField.get(Byte.valueOf((byte)0x00));
                    } else if (retField.getType().equals(short.class)) {
                        return retField.get(Short.valueOf((short)0));
                    } else if (retField.getType().equals(long.class)) {
                        return retField.get(Long.valueOf((long)0));
                    } else if (retField.getType().equals(double.class)) {
                        return retField.get(Double.valueOf((double)0));
                    } else if (retField.getType().equals(char.class)) {
                        return retField.get(Character.valueOf((char)0));
                    }

                } else {
                    currentObject = retField.get(stack.bottom().getFirst());
                }

            }

            return currentObject;

        } else {

            Field retField = stack.peek().getFirst().getClass().getDeclaredField(fieldName);
            retField.setAccessible(true);
            Object ret = retField.get(stack.peek().getFirst());
            return ret;
        }

    }

    private boolean isMethod(String statement) {
        return  (statement.contains("(") && statement.contains(")"));
    }

    private Object execStatement(String statement)  throws NoSuchFieldException, IllegalAccessException, InstantiationException, InvocationTargetException {

        if(!isMethod(statement)) {
            return getField(statement);
        }

        Object ret = null;

        int openParenthesis = statement.indexOf('(');
        String methodPath = statement.substring(0, openParenthesis);

        Object field = null;
        String methodName = null;


        if(methodPath.contains(".") && !methodPath.contains("this")) {
            int lastDot = methodPath.lastIndexOf(".");
            String fieldName = methodPath.substring(0, lastDot);
            field = getField(fieldName);
            methodName = methodPath.substring(lastDot + 1);
        } else {
            field = stack.peek().getFirst();
            methodName = methodPath;
        }


        //find method
        Method[] methods = field.getClass().getMethods();

        Method invoked = null;

        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                invoked = method;
            }
        }

        if (invoked != null) {

                //args
                String argsString = statement.substring(openParenthesis + 1, statement.length() - 1);

            if (argsString.isEmpty()) { //method has no args
                if (invoked.getReturnType().equals(void.class)) {
                    invoked.invoke(field);
                } else {
                    ret = invoked.invoke(field);
                }
            } else {

                String[] argArray = null;
                List<Object> evaluatedArgs = new LinkedList<>();

                if (argsString.contains(",")) {
                    argArray = argsString.split(",");
                } else if (!argsString.isEmpty()) { //one arg
                    argArray = new String[]{argsString};
                } else if (argsString.isEmpty()) {
                    argArray = new String[]{};
                }

                for (String arg : argArray) {

                    arg = arg.trim();

                    if (isMethod(arg)) {
                        evaluatedArgs.add(execStatement(arg));
                    } else if (arg.equals("this")) {
                        evaluatedArgs.add(stack.peek().getFirst());
                    } else if (arg.startsWith("lit ")) {//TODO regexp literal args


                        arg = arg.substring(4);
                        Number num;

                        try {
                            num = Integer.parseInt(arg);
                        } catch (NumberFormatException e) {
                            num = Float.parseFloat(arg);
                        }

                        evaluatedArgs.add(num);

                    } else {
                        evaluatedArgs.add(getField(arg));
                    }

                }

                ret = invoked.invoke(field, evaluatedArgs.toArray());
            }

        }

        return ret;
    }

}
