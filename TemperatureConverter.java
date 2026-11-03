import java.util.Locale;
import java.util.Scanner;

public class TemperatureConverter {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Source scale (C, F, or K): ");
            String sourceScale = normalizeScale(scanner.nextLine());

            System.out.print("How many temperatures? ");
            int count = Integer.parseInt(scanner.nextLine().trim());
            if (count <= 0) {
                throw new IllegalArgumentException("The count must be greater than zero.");
            }

            double[] temperatures = new double[count];
            for (int i = 0; i < count; i++) {
                System.out.print("Temperature " + (i + 1) + ": ");
                temperatures[i] = Double.parseDouble(scanner.nextLine().trim());
                validateTemperature(temperatures[i], sourceScale);
            }

            String firstTarget;
            String secondTarget;
            if (sourceScale.equals("C")) {
                firstTarget = "F";
                secondTarget = "K";
            } else if (sourceScale.equals("F")) {
                firstTarget = "C";
                secondTarget = "K";
            } else {
                firstTarget = "C";
                secondTarget = "F";
            }

            double[] firstResults = convertMultiple(temperatures, sourceScale, firstTarget);
            double[] secondResults = convertMultiple(temperatures, sourceScale, secondTarget);

            System.out.println("\nConversion results:");
            for (int i = 0; i < count; i++) {
                System.out.printf(Locale.US,
                        "%.2f%s is equivalent to %.2f%s and %.2f%s.%n",
                        temperatures[i], unit(sourceScale),
                        firstResults[i], unit(firstTarget),
                        secondResults[i], unit(secondTarget));
            }

            // The method changes its local parameter, but the original value stays the same.
            double original = temperatures[0];
            double converted = convertTemperature(original, sourceScale, firstTarget);
            System.out.printf(Locale.US,
                    "%nPass-by-value: original = %.2f%s, converted result = %.2f%s.%n",
                    original, unit(sourceScale), converted, unit(firstTarget));

            if (count >= 2) {
                double[] pair = convertMultiple(temperatures[0], temperatures[1],
                        sourceScale, firstTarget);
                System.out.printf(Locale.US,
                        "Two-value overload: %.2f%s and %.2f%s.%n",
                        pair[0], unit(firstTarget), pair[1], unit(firstTarget));
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid number. Use a whole number for the count and numeric temperatures.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }

        scanner.close();
    }

    public static double convertCelsius(double celsius, String targetScale) {
        validateTemperature(celsius, "C");
        String target = normalizeScale(targetScale);
        if (target.equals("F")) {
            celsius = celsius * 9.0 / 5.0 + 32.0;
        } else if (target.equals("K")) {
            celsius = celsius + 273.15;
        }
        return celsius;
    }

    public static double convertFahrenheit(double fahrenheit, String targetScale) {
        validateTemperature(fahrenheit, "F");
        String target = normalizeScale(targetScale);
        if (target.equals("C")) {
            fahrenheit = (fahrenheit - 32.0) * 5.0 / 9.0;
        } else if (target.equals("K")) {
            fahrenheit = (fahrenheit + 459.67) * 5.0 / 9.0;
        }
        return fahrenheit;
    }

    public static double convertKelvin(double kelvin, String targetScale) {
        validateTemperature(kelvin, "K");
        String target = normalizeScale(targetScale);
        if (target.equals("C")) {
            kelvin = kelvin - 273.15;
        } else if (target.equals("F")) {
            kelvin = kelvin * 9.0 / 5.0 - 459.67;
        }
        return kelvin;
    }

    public static double[] convertMultiple(double[] temperatures, String sourceScale,
            String targetScale) {
        if (temperatures == null) {
            throw new IllegalArgumentException("The temperature array cannot be null.");
        }
        String source = normalizeScale(sourceScale);
        String target = normalizeScale(targetScale);

        // Java copies the array reference. A new result array keeps the input unchanged.
        double[] results = new double[temperatures.length];
        for (int i = 0; i < temperatures.length; i++) {
            results[i] = convertTemperature(temperatures[i], source, target);
        }
        return results;
    }

    // This overload accepts two temperatures and uses the array version.
    public static double[] convertMultiple(double first, double second, String sourceScale,
            String targetScale) {
        return convertMultiple(new double[] {first, second}, sourceScale, targetScale);
    }

    private static double convertTemperature(double temperature, String sourceScale,
            String targetScale) {
        if (sourceScale.equals("C")) {
            return convertCelsius(temperature, targetScale);
        } else if (sourceScale.equals("F")) {
            return convertFahrenheit(temperature, targetScale);
        }
        return convertKelvin(temperature, targetScale);
    }

    // Use C, F, and K internally, even when a full scale name is entered.
    private static String normalizeScale(String scale) {
        if (scale == null) {
            throw new IllegalArgumentException("Scale must be C, F, or K.");
        }
        String result = scale.trim().toUpperCase(Locale.ROOT);
        if (result.equals("CELSIUS")) {
            result = "C";
        } else if (result.equals("FAHRENHEIT")) {
            result = "F";
        } else if (result.equals("KELVIN")) {
            result = "K";
        }
        if (!result.equals("C") && !result.equals("F") && !result.equals("K")) {
            throw new IllegalArgumentException("Scale must be C, F, or K.");
        }
        return result;
    }

    // Reject non-finite values and temperatures below absolute zero.
    private static void validateTemperature(double temperature, String scale) {
        if (!Double.isFinite(temperature)) {
            throw new IllegalArgumentException("Temperature must be a finite number.");
        }
        if ((scale.equals("C") && temperature < -273.15)
                || (scale.equals("F") && temperature < -459.67)
                || (scale.equals("K") && temperature < 0.0)) {
            throw new IllegalArgumentException("Temperature cannot be below absolute zero.");
        }
    }

    private static String unit(String scale) {
        if (scale.equals("K")) {
            return "K";
        }
        return "\u00B0" + scale;
    }
}
