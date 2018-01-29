package ra;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import static org.lwjgl.opencl.CL10.*;
import org.lwjgl.opencl.CLCapabilities;
import org.lwjgl.system.MemoryStack;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.memUTF8;

public class OpenCLHelp {
    
    public static long getDevice(long platform, CLCapabilities platformCaps, int deviceType) {
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pi = stack.mallocInt(1);
            clGetDeviceIDs(platform, deviceType, null, pi);
            PointerBuffer devices = stack.mallocPointer(pi.get(0));
            clGetDeviceIDs(platform, deviceType, devices, (IntBuffer)null);
            return devices.get(0);
        }
    }

    public static long getDeviceInfoLong(long cl_device_id, int param_name) {
        try ( MemoryStack stack = stackPush() ) {
            LongBuffer pl = stack.mallocLong(1);
            clGetDeviceInfo(cl_device_id, param_name, pl, null);
            return pl.get(0);
        }
    }
    
    public static List<Long> getDevices(long platform, int deviceType) {
        List<Long> devices;
        try (MemoryStack stack = stackPush()) {
            IntBuffer pi = stack.mallocInt(1);
            if (clGetDeviceIDs(platform, deviceType, null, pi) == CL_DEVICE_NOT_FOUND) {
                devices = Collections.emptyList();
            }
            else {
                PointerBuffer deviceIDs = stack.mallocPointer(pi.get(0));
                clGetDeviceIDs(platform, deviceType, deviceIDs, (IntBuffer)null);
                devices = new ArrayList<>(deviceIDs.capacity());

                for ( int i = 0; i < deviceIDs.capacity(); i++) {
                    devices.add(deviceIDs.get(i));
                }
            }
        }
        return devices;
    }
    
    public static String getPlatformInfoStringUTF8(long cl_platform_id, int param_name) {
        try (MemoryStack stack = stackPush()) {
            PointerBuffer pp = stack.mallocPointer(1);
            clGetPlatformInfo(cl_platform_id, param_name, (ByteBuffer)null, pp);
            int bytes = (int) pp.get(0);
            ByteBuffer buffer = stack.malloc(bytes);
            clGetPlatformInfo(cl_platform_id, param_name, buffer, null);
            return memUTF8(buffer, bytes - 1);
        }
    }
    
    private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
        ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }
    
    public static String readFile(String location) {
        StringBuilder source = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(Consts.FILES_PATH + location))) {
            String line;
            while ((line = reader.readLine()) != null) {
                source.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
        return source.toString();
    }
}
