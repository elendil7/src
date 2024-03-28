import java.lang.reflect.Method;

import annotations.BeforeSolving;
import processors.AfterSolvingProcessor;
import processors.BeforeSolvingProcessor;
import utils.PrettyStringUtils;

public class Launcher {
  public static void main(String[] args) {
    ExerciseSolver exerciseSolver = new ExerciseSolver();
    Method[] methods = exerciseSolver.getClass().getDeclaredMethods();
    for (Method method : methods) {
      if (method.isAnnotationPresent(BeforeSolving.class)) {
        // grab method name to lower case
        String methodName = method.getName().toLowerCase();

        // check if runExercise[n] is true. If so, process the method along with its
        // annotation(s)
        try {
          if ((boolean) exerciseSolver.getClass().getField(methodName + "Enabled").get(exerciseSolver)) {
            // print the exercise name, passing last 1 char of method name as an int
            PrettyStringUtils.printExerciseTitle(Integer.parseInt(methodName.substring(8)));

            // run before solving processor
            BeforeSolvingProcessor.process(exerciseSolver.getExerciseFileData(method.getName().toLowerCase()));
            try {
              // run the method
              method.invoke(exerciseSolver);

              // run after solving processor
              AfterSolvingProcessor.process(exerciseSolver.getDecodedString());
              // set the decoded string to nothing
              exerciseSolver.setDecodedString("");
            } catch (Exception e) {
              System.out.println("Error invoking method " + methodName);
              e.printStackTrace();
            }
          }
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
          System.out.println("Field for toggling exercise " + methodName.substring(8) + " not found.");
          e.printStackTrace();
        }

      }
    }
  }
}