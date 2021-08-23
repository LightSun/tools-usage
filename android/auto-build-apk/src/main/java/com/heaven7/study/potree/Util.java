package com.heaven7.study.potree;

/*public*/ class Util {

    //metaPath like: ../pointclouds/h7_test/page_h7/metadata.json
    //pageName: "page_h7"
    public static String generate(String metaPath, String pageName){
        String content = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "\t<meta charset=\"utf-8\">\n" +
                "\t<meta name=\"description\" content=\"\">\n" +
                "\t<meta name=\"author\" content=\"\">\n" +
                "\t<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\">\n" +
                "\t<title>Potree Viewer</title>\n" +
                "\n" +
                "\t<link rel=\"stylesheet\" type=\"text/css\" href=\"../build/potree/potree.css\">\n" +
                "\t<link rel=\"stylesheet\" type=\"text/css\" href=\"../libs/jquery-ui/jquery-ui.min.css\">\n" +
                "\t<link rel=\"stylesheet\" type=\"text/css\" href=\"../libs/openlayers3/ol.css\">\n" +
                "\t<link rel=\"stylesheet\" type=\"text/css\" href=\"../libs/spectrum/spectrum.css\">\n" +
                "\t<link rel=\"stylesheet\" type=\"text/css\" href=\"../libs/jstree/themes/mixed/style.css\">\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "\t<script src=\"../libs/jquery/jquery-3.1.1.min.js\"></script>\n" +
                "\t<script src=\"../libs/spectrum/spectrum.js\"></script>\n" +
                "\t<script src=\"../libs/jquery-ui/jquery-ui.min.js\"></script>\n" +
                "\t<script src=\"../libs/other/BinaryHeap.js\"></script>\n" +
                "\t<script src=\"../libs/tween/tween.min.js\"></script>\n" +
                "\t<script src=\"../libs/d3/d3.js\"></script>\n" +
                "\t<script src=\"../libs/proj4/proj4.js\"></script>\n" +
                "\t<script src=\"../libs/openlayers3/ol.js\"></script>\n" +
                "\t<script src=\"../libs/i18next/i18next.js\"></script>\n" +
                "\t<script src=\"../libs/jstree/jstree.js\"></script>\n" +
                "\t<script src=\"../build/potree/potree.js\"></script>\n" +
                "\t<script src=\"../libs/plasio/js/laslaz.js\"></script>\n" +
                "\t\n" +
                "\t<!-- INCLUDE ADDITIONAL DEPENDENCIES HERE -->\n" +
                "\t<!-- INCLUDE SETTINGS HERE -->\n" +
                "\t\n" +
                "\t<div class=\"potree_container\" style=\"position: absolute; width: 100%; height: 100%; left: 0px; top: 0px; \">\n" +
                "\t\t<div id=\"potree_render_area\" style=\"background-image: url('../build/potree/resources/images/background.jpg');\"></div>\n" +
                "\t\t<div id=\"potree_sidebar_container\"> </div>\n" +
                "\t</div>\n" +
                "\t\n" +
                "\t<script>\n" +
                "\t\n" +
                "\t\twindow.viewer = new Potree.Viewer(document.getElementById(\"potree_render_area\"));\n" +
                "\t\t\n" +
                "\t\tviewer.setEDLEnabled(true);\n" +
                "\t\tviewer.setFOV(60);\n" +
                "\t\tviewer.setPointBudget(2_000_000);\n" +
                "\t\t<!-- INCLUDE SETTINGS HERE -->\n" +
                "\t\tviewer.loadSettingsFromURL();\n" +
                "\t\t\n" +
                "\t\tviewer.setDescription(\"\");\n" +
                "\t\t\n" +
                "\t\tviewer.loadGUI(() => {\n" +
                "\t\t\tviewer.setLanguage('en');\n" +
                "\t\t\t$(\"#menu_appearance\").next().show();\n" +
                "\t\t\t$(\"#menu_tools\").next().show();\n" +
                "\t\t\t$(\"#menu_clipping\").next().show();\n" +
                "\t\t\tviewer.toggleSidebar();\n" +
                "\t\t});\n" +
                "\t\t\n" +
                "\t\t\n" +
                "\n" +
                //"\t\tPotree.loadPointCloud(\"../pointclouds/h7_test/page_h7/metadata.json\", \"page_h7\", e => {\n" +
                "\t\tPotree.loadPointCloud(\""+ metaPath + "\", \""+ pageName+"\", e => {\n" +
                "\t\t\tlet scene = viewer.scene;\n" +
                "\t\t\tlet pointcloud = e.pointcloud;\n" +
                "\t\t\t\n" +
                "\t\t\tlet material = pointcloud.material;\n" +
                "\t\t\tmaterial.size = 1;\n" +
                "\t\t\tmaterial.pointSizeType = Potree.PointSizeType.ADAPTIVE;\n" +
                "\t\t\tmaterial.shape = Potree.PointShape.SQUARE;\n" +
                "\t\t\tmaterial.activeAttributeName = \"rgba\";\n" +
                "\t\t\t\n" +
                "\t\t\tscene.addPointCloud(pointcloud);\n" +
                "\t\t\t\n" +
                "\t\t\tviewer.fitToScreen();\n" +
                "\t\t});\n" +
                "\n" +
                "\t\t\n" +
                "\t\t\n" +
                "\t</script>\n" +
                "\t\n" +
                "\t\n" +
                "  </body>\n" +
                "</html>\n";

        return content;
    }
}
