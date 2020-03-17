The groovy scripts on this page were tested in the [ICF Next Groovy Console](https://github.com/icfnext/aem-groovy-console).

- [find-missing-assets.groovy](find-missing-assets.groovy) - this script iterates the items in the filter of a workflow package and outputs the assets or pages that cannot be found in the JCR.
- [fix-property-using-query.groovy](fix-property-using-query.groovy) - Uses a JCR query to find a node (asset) and then changes the property on a grandchild node. A good example of query execution.
- [fix-multivalue-property-using-query.groovy](fix-property-using-query.groovy) - Uses a JCR query to find a node (asset) and then changes the property from single-value type to multi-value type on a grandchild node. A good example of query execution.
- [copy-property.groovy](copy-property.groovy) - Traverses pages looking for 6.3 redirect property and copies the found value to the 6.5 redirect property.
