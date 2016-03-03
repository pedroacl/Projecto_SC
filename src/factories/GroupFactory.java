package factories;

import entities.Group;

public class GroupFactory {
	private static GroupFactory groupFactory = new GroupFactory();
	
	private static Long groupId;
	
	private GroupFactory() {
		groupId = 1L;
	}

	public static GroupFactory getInstance() {
		return groupFactory;
	}
	
	public Group build(String groupName, String admin) {
		Group group = new Group(groupName, admin);
		group.setId(groupId);
		groupId++;
		
		return group;
	}
}
