package dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import entities.Group;
import factories.ConversationFactory;
import factories.GroupFactory;
import interfaces.dao.GroupDAOInterface;
import util.MiscUtil;

public class GroupDAO implements GroupDAOInterface {

	private static GroupDAO groupDAO = new GroupDAO();
	
	private static ConversationFactory conversationFactory;

	private GroupDAO() {
		conversationFactory = conversationFactory.getInstance();
	}

	public static GroupDAO getInstance() {
		return groupDAO;
	}

	/**
	 * 
	 * @param groupName
	 * @return
	 */
	@Override
	public Group getGroupByName(String groupName) {
		//TODO

		return null;
	}

	/**
	 * Adicionar utilizador a um grupo
	 * 
	 * @param groupName
	 * @param username
	 */
	@Override
	public boolean addUserToGroup(String username, String groupName) {
		String filePath = "groups/" + groupName + "/group";
		
		Group group = (Group) MiscUtil.readObject(filePath);
		
		if (group.addUser(username)){
			MiscUtil.writeObject(group, filePath);
		} else {
			return false;
		}
		
		return true;
	}

	/**
	 * Função que permite obter um hashmap com todos os grupos registados
	 */
	@Override
	public HashMap<String, String> getGroups() {
		HashMap<String, String> groups = new HashMap<String, String>();
		
		String line;
		BufferedReader br;

		MiscUtil.createFile("groups.txt");
		
		//carregar utilizadores
		File file = new File("groups.txt");
	
		//nao existe ficheiro
		if (!file.exists())
			System.out.println("Nao existem grupos adicionados.");

		try {
			FileReader fr = new FileReader(file);
			br = new BufferedReader(fr);
			
			while ((line = br.readLine()) != null) {
				String[] args = line.split(":");
				String groupname = args[0];
				String owner = args[1];
				
				groups.put(groupname, owner);
				System.out.println(groupname + " " + owner);
				
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return groups;

	}

	/**
	 * Função que permite criar um grupo
	 */
	@Override
	public Long createGroup(String groupName, String admin) {
		
		//adiciona uma entrada no ficheiro groups.txt
		try {
		FileWriter fw = new FileWriter("groups.txt", true);
		fw.write(groupName+ ":" + admin + "\n");
		fw.close();
		}catch (IOException e) {
			e.printStackTrace();
		}	
		
		//cria directoria groups se não existir ainda
		MiscUtil.createDir("groups");
		
		//cria group
		long conversationId = conversationFactory.generateID();
		Group novoGrupo = new Group(groupName, admin, conversationId);
		
		//Persiste grupo na directoria groups
		MiscUtil.createDir("groups/" + groupName);
		MiscUtil.writeObject(novoGrupo, "groups/" + groupName + "/group");
		
		//Cria directoria da convresaçao com o respectivo id na pasta de conversaçoes
		MiscUtil.createDir("conversations/" + conversationId);
		MiscUtil.createDir("conversations/" + conversationId + "/messages");
		MiscUtil.createFile("conversations/" + conversationId + "/conversation");
		
		return conversationId;
	}

	public void deleteGroup(String groupName) {
		//apaga entrada no ficheiro groups.txt
		File file = new File("groups.txt");
		File tempFile = new File("tempGroups.txt");
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
			
			String line;
			
			//copiar todos os users para o ficheiro temp
			while((line = reader.readLine()) != null) {
				String currentUsername = line.split(":")[0];

				//nao copiar user
				if (currentUsername.equals(groupName))
					continue;
					
			    writer.write(line);
			}
			writer.close();
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Apaga ficheiro antigo
		if (!file.delete()) {
	        System.out.println("Could not delete file");
	        return;
	      }
		if (!tempFile.renameTo(file))
	        System.out.println("Could not rename file");

		//eliminar pasta do group
		file = new File("groups/" + groupName);
		if(!file.delete());
			System.out.println("não foi possivel apagar pasta do grupo");
		
	}
}
