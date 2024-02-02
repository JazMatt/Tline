package org.example.tline.threads;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.*;
import com.sun.jna.ptr.IntByReference;

public class GetCurrentApp implements Runnable {

    private UpdateDataBase updateDB;
    @Override
    public void run() {

        updateDB = new UpdateDataBase();

        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            WinDef.HWND hwnd = User32.INSTANCE.GetForegroundWindow();

            // GET PATH OF ACTIVE APP
            IntByReference processId = new IntByReference();
            User32.INSTANCE.GetWindowThreadProcessId(hwnd, processId);
            WinNT.HANDLE processHandle = Kernel32.INSTANCE.OpenProcess(
                    WinNT.PROCESS_QUERY_INFORMATION | WinNT.PROCESS_VM_READ, false, processId.getValue());

            char[] exePath = new char[1024];
            Psapi.INSTANCE.GetModuleFileNameExW(processHandle, null, exePath, exePath.length);
            Kernel32.INSTANCE.CloseHandle(processHandle);

            String exePathStr = Native.toString(exePath);
//        System.out.println("Path: " + exePathStr);  // "C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2023.3.2\bin\idea64.exe"

            // FORMAT IT
            String activeAppNameNotFormatted = exePathStr.substring(exePathStr.lastIndexOf("\\") + 1);
            String activeAppName = activeAppNameNotFormatted.replace(".exe", "").toLowerCase();
            char firstChar = Character.toUpperCase(activeAppName.charAt(0));
            activeAppName = firstChar + activeAppName.substring(1);

            if (activeAppName.endsWith("64") || activeAppName.endsWith("32")) {
                activeAppName = activeAppName.substring(0, activeAppName.length() - 2);
            }
            System.out.println("Not formatted: " + activeAppNameNotFormatted);
            System.out.println("Formatted: " + activeAppName);

        }
    }
}
