package plz.lizi.objhandles.api.common;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

public class EmptyTransformer implements ClassFileTransformer {
	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
		return classfileBuffer;
	}

	@Override
	public byte[] transform(Module module, ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
		return classfileBuffer;
	}
}
