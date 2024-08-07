package business;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GroupHandler {
    private Map<String, List<String>> groups = new ConcurrentHashMap<>();
    private Map<String, List<String>> pendingNicknamesForGroup = new ConcurrentHashMap<>();

    public void addGroup(String groupName, List<String> members) {
        groups.put(groupName, members);
    }

    public void addNicknameInPendingGroup(String groupNickname, String receiver) {
        if (!groups.containsKey(groupNickname)) {
            if (!pendingNicknamesForGroup.containsKey(groupNickname)) {
                pendingNicknamesForGroup.put(groupNickname, new ArrayList<>());
            }
            List<String> pendingIps = pendingNicknamesForGroup.get(groupNickname);
            synchronized (pendingIps) {
                pendingIps.add(receiver);
            }
        } else {
            System.out.println("The group " + "doesn't exists");
        }
    }

    public boolean removeNicknamesInPending(String group, String sender) {
        return pendingNicknamesForGroup.containsKey(group) && pendingNicknamesForGroup.get(group).remove(sender);

    }

    public void addNewMember(String group, String ip) {
        if (groups.containsKey(group)) {
            groups.get(group).add(ip);
        } else {
            System.out.println(group + " doesn't exist");
        }
    }
}
