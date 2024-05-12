<h1 align="center"> OEPP - Ontology Evaluation Protégé Plugin </h1>

<p align="center">

<img src ="https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white">
<img src ="https://img.shields.io/badge/protege-5A0FC8.svg?style=for-the-badge&logo=proteus">
<img src ="https://img.shields.io/badge/Apache%20Maven-C71A36.svg?style=for-the-badge&logo=Apache-Maven&logoColor=white">

</p>

<br>

<p align="center">
    <img src ="/src/main/java/edu/stanford/bmir/protege/examples/Images/OEPP.png"/>
</p>

## Description

OEPP is a Protégé plugin that allows users to evaluate ontologies based on a set of metrics. The plugin provides a set of predefined metrics that users can use to evaluate their ontologies. Each Score is calculated based on the ontology's structure and content and color coded to indicate the quality of the ontology. The plugin also provides a detailed explanation of each metric and how it is calculated which can accessed using the `Help` button.

## Installation

1. Download the latest release of the plugin from the [releases page](https://github.com/lakshaybhushan/oepp/releases/tag/v.1.0.0).

2. Open your Protégé application directory and navigate to the `plugins` folder and copy the downloaded `.jar` file into the `plugins` folder.

3. Open Protégé and go to `File -> Check for plugins` and click on the `Install plugin` button.

4. After that navigate to view -> tabs -> OEPP to open the plugin.

## Usage

1. Open the ontology that you want to evaluate in Protégé.

2. Click on the `OEPP` tab in the Protégé window.

## Features

- [x] Calculate Ontology Metrics on the go!
- [x] Display Scores with Color Codes
- [x] Detailed Explanation of Metrics with the `Help` button

## Ontology Metrics Calculated

- **Lack of Cohesion in Methods (LCOMOnto):** Measures the semantic and conceptual relatedness of classes in an ontology. A lower score indicates better cohesion.

- **Weighted Method Count (WMCOnto):** Calculates the mean number of properties and relationships per class. Higher values may indicate greater complexity.

- **Depth of Subsumption Hierarchy (DITOnto):** Measures the length of the longest path from the root class to a leaf class. Deeper hierarchies may be more complex.

- **Number of Ancestor Classes (NACOnto):** Calculates the mean number of ancestor classes per leaf class. Reflects the inheritance hierarchy.

- **Number of Children (NOCOnto):** Measures the mean number of direct subclasses per class. Indicates the breadth of the ontology hierarchy.

- **Coupling Between Objects (CBOOnto):** Quantifies the number of related classes. Lower coupling is often desirable for maintainability.

- **Response for a Class (RFCOnto):** Measures the number of properties directly accessible from each class. Reflects class complexity.

- **Number of Properties (NOMOnto):** Calculates the average number of properties per class. Provides insights into property richness.

- **Properties Richness (PROnto):** Evaluates the number of properties defined relative to the total number of relationships and properties.

- **Relationship Richness (RROnto):** The number of usages of object and data properties divided by the number of subclass relationships and properties.

- **Attribute Richness (AROnto):** Measures the mean number of attributes per class. Reflects attribute complexity and richness.

- **Relationships per Class (INROnto):** Calculates the mean number of relationships per class. Indicates relationship complexity.

- **Class Richness (CROnto):** Evaluates the mean number of instances per class.Reflects instance diversity and abundance.

- **Ancestors per Class (POnto):** Evaluates the number of ancestors per class by dividing the number of superclasses per each class.

- **Annotation Richness (ANOnto):** Measures the mean number of annotations per class. Reflects the extent of metadata associated with classes.

- **Tangledness (TMOnto):** Evaluates the mean number of parents per class, considering multiple inheritance. Reflects class hierarchy complexity.


## Subcharacteristics of Ontology Metrics

- **Modularity:** The extent to which an ontology can be divided into smaller, independent modules.

- **Reusability:** The extent to which an ontology can be reused in different contexts.

- **Analyzability:** The extent to which an ontology can be analyzed for errors and inconsistencies.

- **Changeability:** The extent to which an ontology can be changed without affecting other parts of the ontology.

- **Modification Stability:** The extent to which an ontology can be modified without affecting other parts of the ontology.

- **Testability:** The extent to which an ontology can be tested for correctness and completeness.

- **Overall Score:** The overall score of the ontology based on the calculated metrics.

## Documentation

The documentation for the plugin can be found at [OEPP Documentation](https://oepp-docs.vercel.app).

## Contributors

- [Lakshay Bhushan](https://github.com/lakshaybhushan)
- [Daksh Sammi](https://github.com/dakshsammi)

