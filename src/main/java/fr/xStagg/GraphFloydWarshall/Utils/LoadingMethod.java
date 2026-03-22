package fr.xStagg.GraphFloydWarshall.Utils;

/**
 * Énumération des méthodes de chargement disponibles pour un graphe.
 * <ul>
 *   <li>{@link #RESOURCES} : chargement depuis le classpath (resources Maven/Gradle)</li>
 *   <li>{@link #FOLDERS}   : chargement depuis le système de fichiers</li>
 * </ul>
 */
public enum LoadingMethod {

    /** Chargement depuis les ressources embarquées dans le classpath. */
    RESOURCES,

    /** Chargement depuis un chemin absolu ou relatif sur le système de fichiers. */
    FOLDERS
}