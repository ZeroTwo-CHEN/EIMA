package top.remake.entity;

/**
 * 返回重命名时用户选择的名称前缀、起始编号、编号位数
 */
public class RenameData {
    private final String name;
    private final int startNum;
    private final int digit;
    public RenameData(String name, int start, int digit){
        this.name=name;
        this.startNum=start;
        this.digit=digit;
    }

    public int getStartNum() {
        return startNum;
    }

    public String getName() {
        return name;
    }

    public int getDigit() {
        return digit;
    }
}
