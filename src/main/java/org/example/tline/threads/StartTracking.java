package org.example.tline.threads;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.*;
import com.sun.jna.ptr.IntByReference;

import java.util.TreeMap;

public class StartTracking implements Runnable {

    private DataBase dataBase;
    volatile private TreeMap<String, String[]> usageData = new TreeMap<>();
    @Override
    public void run() {

        dataBase = new DataBase();
        int secs = 0;

        while (true) {

            try {
                Thread.sleep(1000);
                secs ++;
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

            // Update usageData (add 1 second)
            if (usageData.containsKey(exeName)) {
                int time = Integer.parseInt(usageData.get(exeName)[0]);
                usageData.put(exeName, new String[]{String.valueOf(time + 1), null});
            } else {
                usageData.put(exeName, new String[]{String.valueOf(1), null});
            }

            // Upload dataUsage to database every minute
            if (secs >= 5) {
                System.out.println(usageData);
                TreeMap<String, String[]> usageDataCopy = new TreeMap<>(usageData);
                dataBase.uploadDataToDB(usageDataCopy);
                usageData.clear();
                usageData = new TreeMap<>();
                secs = 0;
            }

        }
    }
}

