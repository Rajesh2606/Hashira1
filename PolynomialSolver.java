import org.json.JSONObject;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class PolynomialSolver {

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Usage: java PolynomialSolver <testcase.json>");
            return;
        }

        String filename = args[0];
        String content = new String(Files.readAllBytes(Paths.get(filename)));
        JSONObject json = new JSONObject(content);

        JSONObject keys = json.getJSONObject("keys");
        int n = keys.getInt("n");
        int k = keys.getInt("k");
        int m = k - 1;  

        List<BigInteger> xs = new ArrayList<>();
        List<BigInteger> ys = new ArrayList<>();
        for (int i = 1; i <= n && xs.size() < k; i++) {
            if (!json.has(String.valueOf(i))) continue;
            JSONObject obj = json.getJSONObject(String.valueOf(i));
            int base = obj.getInt("base");
            String value = obj.getString("value");
            BigInteger y = new BigInteger(value, base);
            BigInteger x = BigInteger.valueOf(i); // index acts as x
            xs.add(x);
            ys.add(y);
        }

        BigInteger[] coeffs = new BigInteger[m + 1];
        Arrays.fill(coeffs, BigInteger.ZERO);

        for (int i = 0; i < k; i++) {
            BigInteger[] basis = new BigInteger[m + 1];
            Arrays.fill(basis, BigInteger.ZERO);
            basis[0] = BigInteger.ONE;

            BigInteger denom = BigInteger.ONE;
            for (int j = 0; j < k; j++) {
                if (i == j) continue;
                BigInteger xi = xs.get(i), xj = xs.get(j);

                BigInteger[] newBasis = new BigInteger[m + 1];
                Arrays.fill(newBasis, BigInteger.ZERO);
                for (int d = 0; d <= m; d++) {
                    if (!basis[d].equals(BigInteger.ZERO)) {
                        if (d + 1 <= m)
                            newBasis[d + 1] = newBasis[d + 1].add(basis[d]);
                        newBasis[d] = newBasis[d].subtract(basis[d].multiply(xj));
                    }
                }
                basis = newBasis;
                denom = denom.multiply(xi.subtract(xj));
            }

          
            BigInteger yi = ys.get(i);
            for (int d = 0; d <= m; d++) {
                if (!basis[d].equals(BigInteger.ZERO)) {
                    coeffs[d] = coeffs[d].add(
                        basis[d].multiply(yi).divide(denom)
                    );
                }
            }
        }

        // Output
        System.out.println("File: " + filename);
        System.out.println("Polynomial degree: " + m);
        System.out.println("Coefficients (from x^0 up to x^" + m + "):");
        for (int i = 0; i <= m; i++) {
            System.out.println("x^" + i + " : " + coeffs[i]);
        }
    }
}