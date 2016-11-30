package net.sourceforge.pmd.dfa.report;

public class PackageNode extends AbstractReportNode {

    private String packageName;

    public PackageNode(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public boolean equalsNode(AbstractReportNode arg0) {
        if (!(arg0 instanceof PackageNode)) {
            return false;
        }

        return ((PackageNode) arg0).getPackageName().equals(this.packageName);
    }

}
