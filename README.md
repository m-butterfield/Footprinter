This is an Eclipse Java RCP project.  I'm still new to RCP so hopefully I can simplify some parts of this (do I really need all those dependencies?  I think no...)  

The program uses the [JH labs Java map projection library] (http://wwww.jhlabs.com/java/maps/proj/index.html) (which is loosely based on the [proj.4] (http://trac.osgeo.org/proj/) C library) for coordinate transformations, which enables it to output a KMKL of polygons representing image footprints when provided with photogrammetric exterior orientation parameters (which are the omega phi kappa - essentially roll, pitch and heading � and x, y, z coordinates of an aerial camera at the exact moment a photo was taken), which can be provided in any coordinate system.  Note: Currently only works for Vexel UCX, more cameras will be added later.

Feel free to try the program out using the "sampleinputfile.txt"  Use ground height of 90 and EPSG code of 2180.  This file is from a flight where the camera was consistently pitched up 8-9 degrees, so you can see the effect this had on image footprints (which was not good!).  

In the future, if I have time to keep messing around with this, I would like to work on some of the following:  

* Add an option to reference a directory containing the actual images so they can be projected onto Google Earth in the KML.
 
* Add some kind of simple "help" accessible from the GUI which describes how the input file can be formatted, where to find EPSG codes, etc...basically just how to use this program...

* Support for more cameras, as well as user created cameras.

* Let the user know the file has been succesfully written.  (also need some proper exception handling)

* I'm not positive this will actually work with every coordinate system...for example, the math for computing corner coordinates may not work if the EO parameters are already in lat/long (this would be easy to fix though - transform into some other coordinate system first, then compute corners and transform back to lat/long), or if transforming into lat/long using the JH labs library works properly with coordinate systems that have units such as US feet.  

* Possible support for custom definitions of input file coordinate system using WKT

* Possible support for exporting ESRI shapefiles (using [gdal] (http://www.gdal.org/) Java bindings or something similar) or other formats instead of KML.  Kind of low priority since KML is pretty universal and this would be simple to do in several applications such as Quantum GIS.

* Speaking of Quantum GIS, this could possibly be translated into a nice Quantum GIS Python plug-in at some point, eliminating the need for a standalone program.  