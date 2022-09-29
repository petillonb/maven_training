= Maven et intégration continue

ifdef::env-github[]
:tip-caption: :bulb:
:note-caption: :information_source:
endif::[]

:hardbreaks-option:

Préfixé par &#x1F4D8;, des "checkpoints" pour vous aider à vérifier que vous avez tout bon.

== Prérequis

* Installer Git (cf https://git-scm.com/book/en/v2/Getting-Started-Installing-Git)
* (Sur Windows, avoir un terminal POSIX type *Git Bash*, la suite de l'exercice est à faire dans celui-ci)
* Configurer Git avec votre nom et email
** `git config --global user.name &quot;John Doe&quot;`
** `git config --global user.email johndoe@example.fr`
* Générer une clé SSH (si absente) et donner la partie publique à GitHub (cf https://help.github.com/articles/connecting-to-github-with-ssh/)

* Installer Java 17 (cf https://adoptium.net/)
* &#x1F4D8; les commandes `javac -version` et `java -version` doivent afficher la version *17*

* Installer Maven (cf https://maven.apache.org/install.html)
* &#x1F4D8; la commande `mvn -version` doit afficher la version *3.8.6*

* Installer IntelliJ Community (cf https://www.jetbrains.com/fr-fr/idea/download)

== Partie 1 - premier commit

* Créer un nouveau dépôt Git public sur la plateforme GitHub avec le nom *maven_training* [.underline]#initialisé# avec un fichier README.md (case à cocher dans le formulaire de création de dépôt)
* Cloner ce nouveau dépôt en utilisant l'*url SSH*
* La branche par défaut est la branche *main* c'est sur celle-ci que nous allons travailler
* Ajouter un fichier *.editorconfig* à la racine du dépôt avec le contenu suivant

[source,EditorConfig]
----
root = true

[*]
end_of_line = lf
insert_final_newline = true

charset = utf-8

indent_style = space
indent_size = 4
----

.A quoi ça sert ?
[%collapsible]
====

[TIP]
=====
Ce fichier (**.editorconfig**) est reconnu par un grand nombre d'IDE (IntelliJ, Eclipse, VS code, etc.) et va permettre de ne pas avoir à se soucier

* du type d'indentation (ici 4 espaces)
* de l'encodage (ici UTF-8)
* du type de fin de ligne (ici `LF`)
* de la ligne vide à la fin de chaque fichier (bonne pratique Git)

Pour plus d'information : https://editorconfig.org/
=====
====

* ajouter un fichier *.gitignore* à la racine du dépôt avec le contenu suivant

[source,gitignore]
----
# IntelliJ
.idea/
*.iml

----

.A quoi ça sert ?
[%collapsible]
====

[TIP]
=====

Ce fichier (**.gitignore**) est reconnu par Git afin d'ignorer les changements des fichiers correspondants.
Dans notre cas, les fichiers que génère IntelliJ ne sont pas nécessaires car :

* un autre IDE (Eclipse, VS code, etc.) n'en aura pas besoin
* le build automatique (CI) n'en a pas besoin
* le projet peut donc être construit sans
=====
====

* faire un commit contenant ces deux fichiers avec le message **Setup project layout**
* pusher ce nouveau commit (sur votre remote par défaut, en l'occurrence GitHub)

== Partie 2 - Maven

Maven est outil qui gère le cycle de vie d'un projet Java (ou d'autres languages sur la JVM par extension).
Le point d'entrée dans un projet pour Maven est le fichier descripteur du projet appelé **pom.xml**.
Pour un projet Java, Maven s'attend à trouver le code de production dans le répertoire **src/main/java** et le code de test dans **src/test/java**.
Tous les fichiers générés par Maven seront placé dans le répertoire **target**.

* créer un fichier **pom.xml** à la racine du dépôt avec le contenu suivant

[source,xml]
----
<?xml version="1.0"?>
<project xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd"
         xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <modelVersion>4.0.0</modelVersion>

    <groupId>fr.lernejo</groupId>
    <artifactId>maven_training</artifactId>
    <version>1.0.0-SNAPSHOT</version>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
    </properties>
</project>

----

* créer un fichier **Sample.java** dans le répertoire **src/main/java/fr/lernejo** avec le contenu suivant

[source,java]
----
package fr.lernejo;

import java.util.function.BiFunction;

public class Sample {

    public int op(Operation op, int a, int b) {
        return op.func.apply(a, b);
    }

    public int fact(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("N should be positive");
        }
        return n == 0 ? 1 : n * fact(n - 1);
    }

    enum Operation {
        ADD((a, b) -> a + b),
        MULT((a, b) -> a * b),
        ;

        final BiFunction<Integer, Integer, Integer> func;

        Operation(BiFunction<Integer, Integer, Integer> func) {
            this.func = func;
        }
    }
}

----

* lancer la commande `mvn compile`
* &#x1F4D8; la classe compilée **Sample.class** correspondante a été générée dans **target/classes/fr/lernejo**
* dans le fichier **.gitignore** ajouter le contenu suivant afin d'ignorer les fichiers générés par Maven

[source,gitignore]
----
# Maven
target/
----
* &#x1F4D8; avec la commande `git status` trois fichiers apparaissent :
** .gitignore
** pom.xml
** src/main/java/fr/lernejo/Sample.java
* faire un commit contenant ces trois modifications avec le message "Setup Maven"

== Partie 3 - Maven wrapper
Afin de pouvoir construire le projet sans avoir besoin d'installer Maven, nous allons utiliser **Maven Wrapper**.
Cet outil permet d'ajouter des scripts (unix et windows) autosuffisant pour le lancement de Maven (comprendre, qui
télécharge les binaires si nécessaires).

* Executer la commande `mvn -N io.takari:maven:0.7.7:wrapper`
* Ajouter à l'index Git les fichiers résultants en s'assurant que le script **mvnw** l'est bien en [.underline]#écriture#
** avec la commande `git update-index --add --chmod=+x mvnw`
** ne pas oublier d'indexer les fichiers générés dans le répertoire **.mvn** visibles avec la commande `ls -al`
* Commiter les fichiers résultants avec le message "Setup Maven Wrapper"

## Partie 4 - CI

L'intégration continue (CI pour Continuous Integration) est un service attaché au projet permet de lancer les
différentes étapes de sa construction à chaque fois qu'un changement est apporté.

Dans cet exercice, nous allons utiliser le service proposé par GitHub.

* créer un fichier **.github/workflows/build.yml** avec le contenu

[source,yml]
----
name: Build

on: push

jobs:
  build:
    name: Build
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - uses: actions/setup-java@v2
        with:
          distribution: 'temurin'
          java-version: '17'
      - uses: actions/cache@v2
        with:
          path: ~/.m2/repository
          key: ${{ runner.os }}-maven-${{ hashFiles('**/pom.xml') }}
          restore-keys: |
            ${{ runner.os }}-maven-
      - run: |
         java -version
         echo $JAVA_HOME
      - run: ./mvnw install
      - uses: codecov/codecov-action@v1
----

.A quoi ça sert ?
[%collapsible]
====

[TIP]
=====
Ce fichier (**build.yml**) est reconnu par GitHub et permet de déclencher à chaque *push* une construction du projet
constituée des étapes suivantes :

* checkout du code
* installation de Java 17
* mise en cache (et récupération) des dépendances Maven du projet
* affichage de la version de java installée (pour info)
* lancement de la commande `mvnw install`
* upload du résultat de la couverture des tests sur Codecov
=====
====

* Commiter ce fichier avec le message "Setup GitHub CI"
* &#x1F4D8; Dans l'interface web GitHub de votre projet, dans l'onglet *Actions*, un nouveau workflow démarre et celui-ci doit se finir en succès

== Partie 5 - Code coverage

* Dans le fichier *pom.xml* ajouter
* les quatre properties suivantes
[source,xml]
----
<properties> <!-- balise existante -->
  ...
  <junit.version>5.9.1</junit.version>
  <assertj.version>3.23.1</assertj.version>

  <maven-surefire-plugin.version>2.22.2</maven-surefire-plugin.version>
  <jacoco-maven-plugin.version>0.8.8</jacoco-maven-plugin.version>
</properties>
----

* les dépendances suivantes

[source,xml]
----
<dependencies>
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>${junit.version}</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.assertj</groupId>
        <artifactId>assertj-core</artifactId>
        <version>${assertj.version}</version>
        <scope>test</scope>
    </dependency>
</dependencies>
----

* et les plugins suivants

[source,xml]
----
<build>
    <pluginManagement>
        <plugins>
            <plugin>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>${maven-surefire-plugin.version}</version>
            </plugin>
            <plugin>
                <groupId>org.jacoco</groupId>
                <artifactId>jacoco-maven-plugin</artifactId>
                <version>${jacoco-maven-plugin.version}</version>
            </plugin>
        </plugins>
    </pluginManagement>
    <plugins>
        <plugin>
            <groupId>org.jacoco</groupId>
            <artifactId>jacoco-maven-plugin</artifactId>
            <executions>
                <execution>
                    <goals>
                        <goal>prepare-agent</goal>
                    </goals>
                </execution>
                <execution>
                    <id>report</id>
                    <phase>test</phase>
                    <goals>
                        <goal>report</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
----

.A quoi ça sert ?
[%collapsible]
====

[TIP]
=====
Nous ajoutons au projet les dépendances

* **junit-jupiter**, un framework servant à écrire et lancer des tests
* **assertj**, une bibliothèque permettant d'écrire des assertions expressives

Par ailleurs, par défaut Maven utilise une version du plugin **surefire** qui ne reconnaît pas **junit-jupiter**, c'est pour ça que nous devons le forcer à une version plus récente.
Enfin, nous utilisons le plugin **jacoco** afin d'analyser la couverture de code et produire le rapport correspondant.
=====
====

* Ouvrir IntelliJ et importer le projet en choisissant `File` -> `New` -> `Project from Existing Sources...` et en sélectionnant le fichier *pom.xml*
* Créer le répertoire src/test/java (clic droit sur `maven_training` -> `New` -> `Directory`) qui va accueillir les classes de test
* Ouvrir la classe Java *Sample* et créer la classe de test correspondante en utilisant le raccourci (Ctrl + Shift + T)
ou par le menu `Navigate` -> `Test`
* Ajouter les tests nécessaires à une couverture du code à 100 %

.Qu'est-ce qu'un test ?
[%collapsible]
====
[TIP]
=====
Un test est constitué de trois parties

* les mises en condition initiale (0..n)
* un élément déclencheur (1)
* des vérifications sur l'état résultant (1..n)

Exemples :
[source,java]
----
   @Test
   void dividing_by_zero_should_produce_an_exception() {
       int dividend = 10;
       int divisor = 0;
       Assertions.assertThatExceptionOfType(DivisionByZeroException.class)
             .isThrownBy(() -> Sample.divide(dividend, divisor));
   }

   @Test
   void dividing_10_by_2_should_produce_5() {
       int dividend = 10; // <1>
       int divisor = 2;
       int quotient = Sample.divide(dividend, divisor); // <2>
       Assertions.assertThat(quotient).as("quotient of 10 / 2")
             .isEqualTo(5); // <3>
   }
----
<1> Mise en condition initiale : on initialise deux variables
<2> Élément déclencheur : la méthode `Sample#divide` est appelée
<3> Vérification : le résultat doit être 5

=====
====

* Commiter ces changements avec le message "Add test to match 100% coverage"

== Partie 6 - Live badges

Pour suivre l'état d'un projet, il peut être plus simple d'afficher des indicateurs visuels sur la page principale.
C'est là l'intérêt des badges. Ces petites images reflètent l'état actuel du projet par rapport aux dernières executions de la CI.

* Dans le fichier README.md ajouter les badges suivants
** build CI (cf https://docs.github.com/en/actions/managing-workflow-runs/adding-a-workflow-status-badge)
** couverture par les tests (cf https://codecov.io/gh/<your-name>/<your-project>/settings/badge)
* Commiter ce changement avec le message "Add live badges"
* &#x1F4D8; Dans l'interface web GitHub de votre projet, le fichier README affiché par défaut doit contenir les deux badges indiquant que le build est en succès et que la couverture par les tests est de 100 %

[TIP]
=====
Si le badge de couverture apparait en gris, il est possible que Codecov n'est pas reçu l'information de couverture.

Pour _troubleshooter_ ce problème, vérifier dans les executions de votre workflow de CI (onglet `Action` dans GitHub), les logs de l'étape correspondante à l'action codecov.
=====
