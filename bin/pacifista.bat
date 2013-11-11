set basePath=%~dp0
java -cp %basePath%pacifista.0.0.25.jar;%basePath%template;%basePath%..\libs\*;%basePath%..\plugins\*;%basePath%..\libs\aws\* org.ukiuni.pacifista.Main -baseDir %basePath%/../ %1 %2 %3 %4 %5 %6 %7 %8 %9
