/*
 * Copyright 2009-2010 László Balázs-Csíki
 *
 * This file is part of Pixelitor. Pixelitor is free software: you
 * can redistribute it and/or modify it under the terms of the GNU
 * General Public License, version 3 as published by the Free
 * Software Foundation.
 *
 * Pixelitor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Pixelitor.  If not, see <http://www.gnu.org/licenses/>.
 */
package pixelitor.utils;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * Creates a skeleton of source code for a filter
 */
public class FilterCreator extends JPanel {
    private JTextField nameTextField;
    private JCheckBox guiCB;
    private JCheckBox parametrizedGuiCB;
    private JCheckBox copySrcCB;
    private JCheckBox runImmediatelyCB;

    private ParamPanel[] paramPanels = new ParamPanel[5];
    private JCheckBox pixelLoopCB;
    private JCheckBox proxyCB;
    private JTextField proxyNameTF;
    private JCheckBox edgeActionCB;
    private JCheckBox interpolationCB;
    private JCheckBox centerSelectorCB;
    private JCheckBox colorCB;

    private FilterCreator() {
        setLayout(new GridBagLayout());

        GridBagHelper.addLabel(this, "Name:", 0, 0);
        nameTextField = new JTextField(20);
        GridBagHelper.addLastControl(this, nameTextField);

        GridBagHelper.addLabel(this, "GUI:", 0, 1);
        guiCB = new JCheckBox();
        guiCB.setSelected(true);
        GridBagHelper.addControl(this, guiCB);

        GridBagHelper.addLabel(this, "Parametrized GUI:", 2, 1);
        parametrizedGuiCB = new JCheckBox();
        parametrizedGuiCB.setSelected(true);
        parametrizedGuiCB.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (parametrizedGuiCB.isSelected()) {
                    guiCB.setSelected(true);
                }
            }
        });
        GridBagHelper.addControl(this, parametrizedGuiCB);

        GridBagHelper.addLabel(this, "Copy Src -> Dest:", 4, 1);
        copySrcCB = new JCheckBox();
        GridBagHelper.addControl(this, copySrcCB);

        GridBagHelper.addLabel(this, "Run Immediately:", 6, 1);
        runImmediatelyCB = new JCheckBox();
        GridBagHelper.addControl(this, runImmediatelyCB);

        GridBagHelper.addLabel(this, "Pixel Loop:", 0, 2);
        pixelLoopCB = new JCheckBox();
        GridBagHelper.addControl(this, pixelLoopCB);

        GridBagHelper.addLabel(this, "Proxy Filter:", 2, 2);
        proxyCB = new JCheckBox();
//        proxyCB.setSelected(true);
        proxyCB.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                proxyNameTF.setEnabled(proxyCB.isSelected());
            }
        });
        GridBagHelper.addControl(this, proxyCB);

        GridBagHelper.addLabel(this, "Proxy Name:", 4, 2);
        proxyNameTF = new JTextField(10);
        GridBagHelper.addControl(this, proxyNameTF);

        GridBagHelper.addLabel(this, "Center Selector:", 0, 3);
        centerSelectorCB = new JCheckBox();
        GridBagHelper.addControl(this, centerSelectorCB);

        GridBagHelper.addLabel(this, "Edge Action:", 2, 3);
        edgeActionCB = new JCheckBox();
        GridBagHelper.addControl(this, edgeActionCB);

        GridBagHelper.addLabel(this, "Interpolation:", 4, 3);
        interpolationCB = new JCheckBox();
        GridBagHelper.addControl(this, interpolationCB);

        GridBagHelper.addLabel(this, "Color:", 6, 3);
        colorCB = new JCheckBox();
        GridBagHelper.addControl(this, colorCB);


        for (int i = 0; i < paramPanels.length; i++) {
            GridBagHelper.addLabel(this, "Param " + (i + 1) + ':', 0, i + 4);
            ParamPanel pp = new ParamPanel();
            paramPanels[i] = pp;
            GridBagHelper.addLastControl(this, pp);
        }
    }

    private ParameterInfo[] getParameterInfoArray() {
        List<ParameterInfo> piList = new ArrayList<ParameterInfo>();
        for (ParamPanel panel : paramPanels) {
            ParameterInfo pi = panel.getParameterInfo();
            if (pi != null) {
                piList.add(pi);
            }
        }
        return piList.toArray(new ParameterInfo[piList.size()]);
    }

    public static void showInDialog(final Frame owner) {
        final FilterCreator filterCreator = new FilterCreator();
        new OKCancelDialog(filterCreator, owner, "Filter Creator", "Show Source", "Close", true) {
            @Override
            protected void dialogAccepted() {
                super.dialogAccepted();
                String s = filterCreator.createFilterSource();
                JTextArea ta = new JTextArea(s);
                JScrollPane sp = new JScrollPane(ta);
                sp.setPreferredSize(new Dimension(sp.getPreferredSize().width + 50, 500));
                JOptionPane.showMessageDialog(this, sp, "Source", JOptionPane.INFORMATION_MESSAGE);
            }

            @Override
            protected void dialogCancelled() {
                super.dialogCancelled();
                dispose();
            }
        };
    }

    public static class ParamPanel extends JPanel {
        private JTextField nameTextField;
        private JTextField minTextField;
        private JTextField maxTextField;
        private JTextField defaultTextField;

        private ParamPanel() {
            setLayout(new FlowLayout(FlowLayout.LEFT));
            add(new JLabel("Name:"));
            nameTextField = new JTextField(20);
            add(nameTextField);
            add(new JLabel("Min:"));
            minTextField = new JTextField("0", 5);
            add(minTextField);
            add(new JLabel("Max:"));
            maxTextField = new JTextField("100", 5);
            add(maxTextField);
            add(new JLabel("Default:"));
            defaultTextField = new JTextField("0", 5);
            add(defaultTextField);
        }

        private ParameterInfo getParameterInfo() {
            String name = nameTextField.getText().trim();
            if (name.length() > 0) {
                int min = Integer.parseInt(minTextField.getText());
                int max = Integer.parseInt(maxTextField.getText());
                int defaultValue = Integer.parseInt(defaultTextField.getText());
                return new ParameterInfo(name, min, max, defaultValue);
            } else {
                return null;
            }
        }
    }

    private String createFilterSource() {
        boolean runFilterImmediately = runImmediatelyCB.isSelected();
        boolean parametrizedGui = parametrizedGuiCB.isSelected();
        boolean gui = guiCB.isSelected();
        boolean copySrc = copySrcCB.isSelected();
        String name = nameTextField.getText();
        boolean pixelLoop = pixelLoopCB.isSelected();
        boolean proxy = proxyCB.isSelected();
        String proxyName = proxyNameTF.getText();

        boolean center = centerSelectorCB.isSelected();
        boolean edge = edgeActionCB.isSelected();
        boolean color = colorCB.isSelected();
        boolean interpolation = interpolationCB.isSelected();

        ParameterInfo[] params = getParameterInfoArray();

        StringBuilder sb = new StringBuilder();

        addImports(sb, name, pixelLoop, parametrizedGui, proxy, proxyName);

        String className = name.replaceAll(" ", "");

        addSuperClass(gui, parametrizedGui, sb, className, proxy);


        if (gui && parametrizedGui) {
            addParamsDeclaration(sb, center, edge, color, interpolation, params);
        }

        if (proxy) {
            sb.append("\n    private ").append(proxyName).append(" filter;\n");
        }

        addConstructor(name, gui, parametrizedGui, sb, className, runFilterImmediately, copySrc, proxy, params);
        addTransform(sb, pixelLoop, proxy, proxyName, center, edge, interpolation, params);
        addGetAdjustPanel(gui, parametrizedGui, sb, className);

        return sb.toString();
    }

    private static void addGetAdjustPanel(boolean gui, boolean parametrizedGui, StringBuilder sb, String className) {
        if (gui && (!parametrizedGui)) {
            sb.append("\n    @Override\n");
            sb.append("    public AdjustPanel getAdjustPanel() {\n");
            sb.append("        return new " + className + "Adjustments(this);\n");
            sb.append("    }\n");
        }

        sb.append('}');
    }

    private static void addTransform(StringBuilder sb, boolean pixelLoop, boolean jhProxy, String proxyName, boolean center, boolean edgeAction, boolean interpolation, ParameterInfo[] params) {
        sb.append("\n    @Override\n");
        sb.append("    public BufferedImage transform(BufferedImage src, BufferedImage dest) {\n");

        for (ParameterInfo param : params) {
            String paramName = param.getParamVariableName();
            String variableName = param.getVariableName();
            sb.append("       int " + variableName + " =  " + paramName + ".getValue();\n");
        }


        if (pixelLoop) {
            sb.append("        int[] srcData = ImageUtils.getPixelsAsArray(src);\n");
            sb.append("        int[] destData = ImageUtils.getPixelsAsArray(dest);\n");
        }

        if (jhProxy) {
            sb.append("       if(filter == null) {\n");
            sb.append("           filter = new " + proxyName + "();\n");
            sb.append("       }\n");

            sb.append('\n');

            if (center) {
                sb.append("        filter.setCentreX(center.getRelativeX());\n");
                sb.append("        filter.setCentreY(center.getRelativeY());\n");
            }
            if (edgeAction) {
                sb.append("        filter.setEdgeAction(edgeAction.getCurrentInt());\n");
            }
            if (interpolation) {
                sb.append("        filter.setInterpolation(interpolation.getCurrentInt());\n");
            }

            sb.append('\n');


            sb.append("        dest = filter.filter(src, dest);\n");
        }

        sb.append("        return dest;\n");
        sb.append("    }\n");
    }

    private static void addSuperClass(boolean gui, boolean parametrizedGui, StringBuilder sb, String className, boolean proxy) {
        String superClassName1;
        if (gui) {
            if (parametrizedGui) {
                superClassName1 = "OperationWithParametrizedGUI";
            } else {
                superClassName1 = "OperationWithGUI";
            }
        } else {
            superClassName1 = "Operation";
        }
        String superClassName = superClassName1;

        if (proxy) {
            className = "JH" + className;
        }
        sb.append("public class ").append(className);
        sb.append(" extends ").append(superClassName);
        sb.append(" {\n");
    }

    private static void addImports(StringBuilder sb, String name, boolean pixelLoop, boolean parametrizedGUI, boolean proxy, String proxyName) {
        if (pixelLoop) {
            sb.append("import pixelitor.utils.ImageUtils;\n");
        }
        sb.append("import pixelitor.filters.gui.ParamSet;\n");
        sb.append("import pixelitor.filters.gui.RangeParam;\n");
        if (parametrizedGUI) {
            sb.append("import pixelitor.filters.OperationWithParametrizedGUI;\n");
        }

        sb.append("\nimport java.awt.image.BufferedImage;\n");
        sb.append("\n/**\n");

        if (proxy) {
            sb.append(" * " + name + " based on the JHLabs " + proxyName + '\n');
        } else {
            sb.append(" * " + name + '\n');
        }

        sb.append(" */\n");
    }

    private static void addConstructor(String name, boolean gui, boolean parametrizedGui, StringBuilder sb, String className, boolean runFiltersImmediately, boolean copySrc, boolean proxy, ParameterInfo... params) {
        if (proxy) {
            className = "JH" + className;
        }
        sb.append("\n    public " + className + "() {\n");

        if (gui && parametrizedGui) {
            sb.append("        super(\"" + name + "\", " + runFiltersImmediately + ");\n");
        } else {
            sb.append("        super(\"" + name + "\");\n");
        }

        if (copySrc) {
            sb.append("        copySrcToDstBeforeRunning = true;\n");
        }

        if (gui && parametrizedGui) {
            addParamSetToConstructor(sb, params);
        }
        sb.append("    }\n");
    }

    private static void addParamSetToConstructor(StringBuilder sb, ParameterInfo... params) {
        if (params.length == 1) {
            sb.append("        paramSet = new ParamSet(" + params[0].getParamVariableName() + ");\n");
        } else {
            sb.append("        paramSet = new ParamSet(\n");
            for (int i = 0; i < params.length; i++) {
                ParameterInfo param = params[i];
                String paramName = param.getParamVariableName();
                sb.append("            " + paramName);
                if (i < params.length - 1) {
                    sb.append(',');
                }
                sb.append('\n');
            }
            sb.append("        );\n");
        }
    }

    private static void addParamsDeclaration(StringBuilder sb, boolean center, boolean edge, boolean color, boolean interpolation, ParameterInfo... params) {

        for (ParameterInfo param : params) {
            String paramVarName = param.getParamVariableName();

            sb.append("    private RangeParam " + paramVarName + " = new RangeParam(\"" + param.getName() + "\", " + param.getMin() + ", " + param.getMax() + ", " + param.getDefaultValue() + ");");

            sb.append('\n');
        }

        if (center) {
            sb.append("    private ImagePositionParam centerParam = new ImagePositionParam(\"Center\");\n");
        }
        if (edge) {
            sb.append("    private IntChoiceParam edgeActionParam =  IntChoiceParam.getEdgeActionChoices();\n");
        }
        if (interpolation) {
            sb.append("    private IntChoiceParam interpolationParam = IntChoiceParam.getInterpolationChoices();\n");
        }
        if (color) {
            sb.append("    private ColorParam colorParam = new ColorParam(\"Color:\", Color.WHITE, false);\n");
        }

    }

    public static void main(String[] args) {
        showInDialog(null);
    }

    public static class ParameterInfo {
        String name;
        String paramVariableName;
        String variableName;
        int min;
        int max;
        int defaultValue;

        public ParameterInfo(String name, int min, int max, int defaultValue) {
            this.name = name;
            this.min = min;
            this.max = max;
            this.defaultValue = defaultValue;

            String tmp = name.replaceAll(" ", "");
            this.variableName = tmp.substring(0, 1).toLowerCase() + tmp.substring(1);
            this.paramVariableName = variableName + "Param";
        }

        private String getName() {
            return name;
        }

        private String getParamVariableName() {
            return paramVariableName;
        }

        private String getVariableName() {
            return variableName;
        }

        private int getMin() {
            return min;
        }

        private int getMax() {
            return max;
        }

        private int getDefaultValue() {
            return defaultValue;
        }
    }
}
