package org.example.tline.functionality;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.*;
import com.sun.jna.ptr.IntByReference;

import java.util.TreeMap;

public class StartTracking implements Runnable {

    private DataBase dataBase;
    public static final int checkingFrequencySeconds = 1;
    private TreeMap<String, String[]> usageData = new TreeMap<>();
    @Override
    public void run() {

        int secs1 = 0;
        int secs10 = 0;
        dataBase = new DataBase();

        while (true) {

            try {
                Thread.sleep(checkingFrequencySeconds * 1000);
                secs1 += checkingFrequencySeconds;
                secs10 += checkingFrequencySeconds;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            // Get path of active app
            WinDef.HWND hwnd = User32.INSTANCE.GetForegroundWindow();
            IntByReference processId = new IntByReference();
            User32.INSTANCE.GetWindowThreadProcessId(hwnd, processId);
            WinNT.HANDLE processHandle = Kernel32.INSTANCE.OpenProcess(
                    WinNT.PROCESS_QUERY_INFORMATION | WinNT.PROCESS_VM_READ, false, processId.getValue());
            char[] exePath = new char[1024];
            Psapi.INSTANCE.GetModuleFileNameExW(processHandle, null, exePath, exePath.length);
            Kernel32.INSTANCE.CloseHandle(processHandle);
            String exePathStr = Native.toString(exePath);
//          System.out.println("Path: " + exePathStr);  // "C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2023.3.2\bin\idea64.exe"

            String exeName = exePathStr.substring(exePathStr.lastIndexOf("\\") + 1).toLowerCase();

            // Update usageData (add time)
            if (usageData.containsKey(exeName)) {
                int time = Integer.parseInt(usageData.get(exeName)[0]);
                usageData.put(exeName, new String[]{String.valueOf(time + checkingFrequencySeconds), null});
            } else {
                usageData.put(exeName, new String[]{String.valueOf(2), null});
            }

            // Refresh dataBase every 10 minutes to check if new file shouldn't be created (because of the next day)
            if (secs10 >= 600) {
                dataBase = new DataBase();
                secs10 = 0;
            }

            // Upload dataUsage to database every minute
            if (secs1 >= 60) {
                TreeMap<String, String[]> usageDataCopy = new TreeMap<>(usageData);
                dataBase.uploadDataToDB(usageDataCopy);
                usageData = new TreeMap<>();
                secs1 = 0;
            }

        }
    }
}

