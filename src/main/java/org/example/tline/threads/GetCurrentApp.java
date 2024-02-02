package org.example.tline.threads;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.*;
import com.sun.jna.ptr.IntByReference;

import java.util.TreeMap;

public class GetCurrentApp implements Runnable {

    private UpdateDataBase updateDB;
    private TreeMap<String, String[]> usageData;
    @Override
    public void run() {

        updateDB = new UpdateDataBase();
        usageData = updateDB.getUsageData();

        System.out.println(usageData);
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

            String exeName = exePathStr.substring(exePathStr.lastIndexOf("\\") + 1);

            // Update usageData (add 1 second)
            if (usageData.keySet().contains(exeName)) {
                int time = Integer.parseInt(usageData.get(exeName)[0]);
                usageData.put(exeName, new String[]{null, String.valueOf(time + 1)});
            } else {
                usageData.put(exeName, new String[]{null, String.valueOf(1)});
            }

            // Upload dataUsage to database every minute
            if (secs >= 5) {
                System.out.println("DATABASE UPDATE\n" + usageData);
                updateDB.uploadDataToDB(usageData);
                usageData.clear();
                secs = 0;
            }

                // Format exeName
//                String formatName = exeName.replace(".exe", "").toLowerCase();
//                char firstChar = Character.toUpperCase(formatName.charAt(0));
//                formatName = firstChar + formatName.substring(1);
//
//                if (formatName.endsWith("64") || formatName.endsWith("32")) {
//                    formatName = formatName.substring(0, formatName.length() - 2);
        }
    }
}

