package ru.mew_hpm.sshfilemanager.dao;

import java.util.HashSet;
import java.util.Set;

public class MountPoint {
    private static final Set<String> blockedFSTypes = new HashSet<String>() {{
        add("swap".toLowerCase());
        add("LVM2_member".toLowerCase());
        add("crypto_LUKS".toLowerCase());
    }};

    private static final Set<String> knownPartTypes = new HashSet<String>() {{
        add("mmcblk[0-9]{1,3}p[0-9]{1,3}");
        add("sd[a-z][0-9]{1,3}");
        add("sr[0-9]{1,3}");
    }};

    private static final Set<String> knownDevTypes = new HashSet<String>() {{
        add("mmcblk[0-9]{1,3}.*");
        add("sd[a-z].*");
        add("sr[0-9].*");
    }};

    private String drive;
    private String folder;
    private String fstype;
    private String size;
    private boolean mounted;
    private boolean swap;
    private boolean empty;
    private boolean system;
    private boolean displayed;
    private boolean rootDevice;

    public MountPoint() {}

    public MountPoint(String line) {
        final String[] part = line.split("\\ ");

        if (part.length >= 3) {
            drive = part[0].trim();
            fstype = part[1].trim();
            size = part[2].trim();
            folder = (part.length > 3) ? part[3].trim() : "";
            mounted = !folder.isEmpty();
            swap = fstype.toLowerCase().contains("swap");
            empty = fstype.isEmpty();
            system = blockedFSTypes.contains(fstype.toLowerCase()) || drive.contentEquals("/") || drive.contentEquals("/home");

            for (String pattern : knownDevTypes) {
                if (drive.matches(pattern)) displayed = true;
            }

            rootDevice = true;
            for (String pattern : knownPartTypes) {
                if (drive.matches(pattern)) rootDevice = false;
            }
        }
    }

    public String getDrive() {
        return drive;
    }

    public void setDrive(String drive) {
        this.drive = drive;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getFstype() {
        return fstype;
    }

    public void setFstype(String fstype) {
        this.fstype = fstype;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public boolean isMounted() {
        return mounted;
    }

    public void setMounted(boolean mounted) {
        this.mounted = mounted;
    }

    public boolean isSwap() {
        return swap;
    }

    public void setSwap(boolean swap) {
        this.swap = swap;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    public boolean isSystem() {
        return system;
    }

    public void setSystem(boolean system) {
        this.system = system;
    }

    public boolean isDisplayed() {
        return displayed;
    }

    public void setDisplayed(boolean displayed) {
        this.displayed = displayed;
    }

    public boolean isRootDevice() {
        return rootDevice;
    }

    public void setRootDevice(boolean rootDevice) {
        this.rootDevice = rootDevice;
    }
}
