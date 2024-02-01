package org.example.tline;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.*;
import com.sun.jna.ptr.IntByReference;

public class AppList {

    public static void main(String[] args) {

        // PRINT TITLE OF ACTIVE WINDOW (NOT WANTED)
        User32 user32 = User32.INSTANCE;
        WinDef.HWND hwnd = user32.GetForegroundWindow();
        char[] title = new char[1024];
        user32.GetWindowText(hwnd, title, title.length);
        System.out.println("Active window: " + String.valueOf(title));
        // "Active window: Project4 - AppList.java"

        // PRINT PATH OF ACTIVE APP AND FORMAT IT (BETTER BUT STILL NOT WANTED)
        IntByReference processId = new IntByReference();
        int id = user32.GetWindowThreadProcessId(hwnd, processId);
        WinNT.HANDLE processHandle = Kernel32.INSTANCE.OpenProcess(
                WinNT.PROCESS_QUERY_INFORMATION | WinNT.PROCESS_VM_READ, false, processId.getValue());
        char[] exePath = new char[1024];
        Psapi.INSTANCE.GetModuleFileNameExW(processHandle, null, exePath, exePath.length);
        String exePathStr = Native.toString(exePath);
        System.out.println(exePathStr);
        // "C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2023.3.2\bin\idea64.exe"

        // FORMAT IT
        String activeAppName = exePathStr.substring(exePathStr.lastIndexOf("\\") + 1)
                .replace(".exe", "").toLowerCase();
        char firstChar = Character.toUpperCase(activeAppName.charAt(0));
        activeAppName = firstChar + activeAppName.substring(1);

        System.out.println(activeAppName);
        Kernel32.INSTANCE.CloseHandle(processHandle);
        // "Idea64" instead of "InteliJ IDEA"
        // "Chrome" instead of "Google Chrome"
        // "Applicationframehost" instead of "Settings"
        // Task manager doesn't work at all
    }
}
