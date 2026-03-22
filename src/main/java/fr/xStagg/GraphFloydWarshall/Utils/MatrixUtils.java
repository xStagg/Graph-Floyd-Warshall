package fr.xStagg.GraphFloydWarshall.Utils;

/**
 * Classe utilitaire fournissant des opérations courantes sur les matrices entières.
 */
public class MatrixUtils {

    /**
     * Retourne une copie profonde d'une matrice entière 2D.
     *
     * @param src matrice source à copier
     * @return nouvelle matrice de même dimensions et valeurs que {@code src}
     */
    public static int[][] copyMatrix(int[][] src) {
        int[][] dst = new int[src.length][src[0].length];
        for (int i = 0; i < src.length; i++) {
            System.arraycopy(src[i], 0, dst[i], 0, src[i].length);
        }
        return dst;
    }

    /**
     * Affiche une matrice entière 2D dans la console, avec un alignement de 6 caractères
     * par cellule pour une lecture facilitée.
     *
     * @param matrix matrice à afficher
     */
    public static void printMatrix(int[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                System.out.printf("%6d", matrix[i][j]);
            }
            System.out.println();
        }
    }
}