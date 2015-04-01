package org.aim.cswrapper.aspect;

public class AspectDescription {

	private String aspectClass; // The aspect that have to be applied
	private boolean attributeExclude;
	private boolean onAbstract;
	private boolean onPrivate;
	private boolean onProtected;
	private boolean onPublic;
	private boolean onStatic;
	private boolean onSynchronized;
	private int priority;
	private String superClass; // Required Super class
	private String superClassAssembly; // Assebly of required Super class
	private String targetAssemblies; // Target assemblies
	private String targetMembers; // Target methods
	private String targetTypes; // Target classes

	public AspectDescription() {
		attributeExclude = false;
		priority = 0;
		onAbstract = false;
		onPrivate = false;
		onProtected = false;
		onPublic = false;
		onStatic = false;
		onSynchronized = false;
	}

	/**
	 * @return the aspectClass
	 */
	public String getAspectClass() {
		return aspectClass;
	}

	/**
	 * @return the priority
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * @return the superClass
	 */
	public String getSuperClass() {
		return superClass;
	}

	/**
	 * @return the superClassAssembly
	 */
	public String getSuperClassAssembly() {
		return superClassAssembly;
	}

	/**
	 * @return the targetAssemblies
	 */
	public String getTargetAssemblies() {
		return targetAssemblies;
	}

	private String getTargetMemberAttributes() {
		StringBuilder builder = new StringBuilder();

		if (onAbstract) {
			builder.append("MulticastAttributes.Abstract");
		}
		if (onPrivate) {
			if (builder.length() > 0)
				builder.append(" | ");
			builder.append("MulticastAttributes.Private");
		}
		if (onProtected) {
			if (builder.length() > 0)
				builder.append(" | ");
			builder.append("MulticastAttributes.Protected");
		}
		if (onPublic) {
			if (builder.length() > 0)
				builder.append(" | ");
			builder.append("MulticastAttributes.Public");
		}
		if (onStatic) {
			if (builder.length() > 0)
				builder.append(" | ");
			builder.append("MulticastAttributes.Static");
		}

		return builder.toString();
	}

	/**
	 * @return the targetMembers
	 */
	public String getTargetMembers() {
		return targetMembers;
	}

	/**
	 * @return the targetTypes
	 */
	public String getTargetTypes() {
		return targetTypes;
	}

	/**
	 * @return the attributeExclude
	 */
	public boolean isAttributeExclude() {
		return attributeExclude;
	}

	/**
	 * @return the onAbstract
	 */
	public boolean isOnAbstract() {
		return onAbstract;
	}

	/**
	 * @return the onPrivate
	 */
	public boolean isOnPrivate() {
		return onPrivate;
	}

	/**
	 * @return the onProtected
	 */
	public boolean isOnProtected() {
		return onProtected;
	}

	/**
	 * @return the onPublic
	 */
	public boolean isOnPublic() {
		return onPublic;
	}

	/**
	 * @return the onStatic
	 */
	public boolean isOnStatic() {
		return onStatic;
	}

	/**
	 * @return the onSynchronized
	 */
	public boolean isOnSynchronized() {
		return onSynchronized;
	}

	/**
	 * @param aspectClass
	 *            the aspectClass to set
	 */
	public void setAspectClass(String aspectClass) {
		this.aspectClass = aspectClass;
	}

	/**
	 * @param attributeExclude
	 *            the attributeExclude to set
	 */
	public void setAttributeExclude(boolean attributeExclude) {
		this.attributeExclude = attributeExclude;
	}

	/**
	 * @param onAbstract
	 *            the onAbstract to set
	 */
	public void setOnAbstract(boolean onAbstract) {
		this.onAbstract = onAbstract;
	}

	/**
	 * @param onPrivate
	 *            the onPrivate to set
	 */
	public void setOnPrivate(boolean onPrivate) {
		this.onPrivate = onPrivate;
	}

	/**
	 * @param onProtected
	 *            the onProtected to set
	 */
	public void setOnProtected(boolean onProtected) {
		this.onProtected = onProtected;
	}

	/**
	 * @param onPublic
	 *            the onPublic to set
	 */
	public void setOnPublic(boolean onPublic) {
		this.onPublic = onPublic;
	}

	/**
	 * @param onStatic
	 *            the onStatic to set
	 */
	public void setOnStatic(boolean onStatic) {
		this.onStatic = onStatic;
	}

	/**
	 * @param onSynchronized
	 *            the onSynchronized to set
	 */
	public void setOnSynchronized(boolean onSynchronized) {
		this.onSynchronized = onSynchronized;
	}

	/**
	 * @param priority
	 *            the priority to set
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}

	/**
	 * @param superClass
	 *            the superClass to set
	 */
	public void setSuperClass(String superClass) {
		this.superClass = superClass;
	}

	/**
	 * @param superClassAssembly
	 *            the superClassAssembly to set
	 */
	public void setSuperClassAssembly(String superClassAssembly) {
		this.superClassAssembly = superClassAssembly;
	}

	/**
	 * @param targetAssemblies
	 *            the targetAssemblies to set
	 */
	public void setTargetAssemblies(String targetAssemblies) {
		this.targetAssemblies = targetAssemblies;
	}

	/**
	 * @param targetMembers
	 *            the targetMembers to set
	 */
	public void setTargetMembers(String targetMembers) {
		this.targetMembers = targetMembers;
	}

	/**
	 * @param targetTypes
	 *            the targetTypes to set
	 */
	public void setTargetTypes(String targetTypes) {
		this.targetTypes = targetTypes;
	}

	@Override
	public String toString() {
		if (aspectClass.isEmpty()) {
			throw new RuntimeException("AspectClass required");
		}

		StringBuilder builder = new StringBuilder();

		builder.append("[assembly: ");

		// Add aspect class
		builder.append(aspectClass);
		// Start attributes
		builder.append("(");

		// ###################################
		// Build attributes
		StringBuilder attrBuilder = new StringBuilder();

		// SuperClass
		if (superClass != null && !superClass.isEmpty()) {
			attrBuilder.append("RequiredSuper = \"").append(superClass).append("\"");
		}

		// SuperClassAssembly
		if (superClassAssembly != null && !superClassAssembly.isEmpty()) {
			if (attrBuilder.length() > 0)
				attrBuilder.append(", ");
			attrBuilder.append("RequiredSuperAssembly = \"").append(superClassAssembly).append("\"");
		}

		// Synchronized
		if (onSynchronized) {
			if (attrBuilder.length() > 0)
				attrBuilder.append(", ");
			attrBuilder.append("RequiredSynchronized = true");
		}

		// TargetTypes
		if (targetTypes != null && !targetTypes.isEmpty()) {
			if (attrBuilder.length() > 0)
				attrBuilder.append(", ");
			attrBuilder.append("AttributeTargetTypes = \"").append(targetTypes).append("\"");
		}

		// TargetAssemblies
		if (targetAssemblies != null && !targetAssemblies.isEmpty()) {
			if (attrBuilder.length() > 0)
				attrBuilder.append(", ");
			attrBuilder.append("AttributeTargetAssemblies = \"").append(targetAssemblies).append("\"");
		}

		// TargetMemberAttributes
		String targetMemberAttributes = getTargetMemberAttributes();
		if (!targetMemberAttributes.isEmpty()) {
			if (attrBuilder.length() > 0)
				attrBuilder.append(", ");
			attrBuilder.append("AttributeTargetMemberAttributes = ").append(targetMemberAttributes);
		}

		// TargetMembers
		if (targetMembers != null && !targetMembers.isEmpty()) {
			if (attrBuilder.length() > 0)
				attrBuilder.append(", ");
			attrBuilder.append("AttributeTargetMembers = \"").append(targetMembers).append("\"");
		}

		// AttributeExclude
		if (attributeExclude) {
			if (attrBuilder.length() > 0)
				attrBuilder.append(", ");
			attrBuilder.append("AttributeExclude = true");
		}

		// Priority
		if (priority > 0) {
			if (attrBuilder.length() > 0)
				attrBuilder.append(", ");
			attrBuilder.append("AttributePriority = ").append(priority);
		}

		// ###################################
		// Add attributes
		builder.append(attrBuilder);

		// Finalize block and return
		return builder.append(")]").toString();
	}
}
