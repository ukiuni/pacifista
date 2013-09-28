set basePath=%~dp1
java -cp %basePath%\pasifista.0.0.19.jar;%basePath%\template;%basePath%\..\libs\*;%basePath%\..\plugins\*;%basePath%\..\libs\aws\* org.ukiuni.pacifista.Main -baseDir %basePath%/../ %1 %2 %3 %4 %5 %6 %7 %8 %9
