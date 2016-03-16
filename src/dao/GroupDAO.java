package dao;

/**
 * 
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import entities.Group;
import factories.ConversationFactory;
import interfaces.dao.GroupDAOInterface;
import util.PersistenceUtil;

/**
 * Classe que gere os grupos, persistindo-os em disco
 *
 */
public class GroupDAO implements GroupDAOInterface {

	private static ConversationFactory conversationFactory;

	public GroupDAO() {
		conversationFactory = ConversationFactory.getInstance();
	}

	/**
	 * Adicionar utilizador a um grupo
	 * 
	 * @param groupName
	 *            Nome do grupo
	 * @param username
	 *            Nome do utilizador a ser adicionado
	 * @return Devolve true caso o username tenha sido adicionado a groupName e
	 *         false caso contrario
	 */
	@Override
	public boolean addUserToGroup(String username, String groupName) {
		String filePath = "groups/" + groupName + "/group";

		Group group = (Group) PersistenceUtil.readObject(filePath);

		if (group.addUser(username)) {
			PersistenceUtil.writeObject(group, filePath);
		} else {
			return false;
		}

		return true;
	}

	/**
	 * Função que permite obter um hashmap com todos os grupos registados
	 * 
	 * @return HasMap com todos os grupos registados associados com os seus
	 *         donos
	 */
	@Override
	public ConcurrentHashMap<String, String> getGroups() {
		ConcurrentHashMap<String, String> groups = new ConcurrentHashMap<String, String>();

		String line;
		BufferedReader br;

		// criar ficheiro caso este nao exista
		PersistenceUtil.createFile("groups.txt");
		File file = new File("groups.txt");

		// existe ficheiro e nao estah vazio
		if (file.exists() && file.length() > 0) {
			try {
				FileReader fr = new FileReader(file);
				br = new BufferedReader(fr);

				// iterar linhas do ficheiro
				// groupName:admin
				while ((line = br.readLine()) != null) {
					// realizar parsing de cada linha
					String[] args = line.split(":");
					String groupname = args[0];
					String owner = args[1];

					groups.put(groupname, owner);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return groups;
	}

	/**
	 * Permite criar um grupo
	 * 
	 * @param groupName
	 *            Nome do grupo a ser criado
	 * @param admin
	 *            Dono do grupo, isto é quem cria o grupo
	 * @return Devolve o id da conversação associada ao grupo
	 */
	@Override
	public Long createGroup(String groupName, String admin) {

		// adiciona uma entrada no ficheiro groups.txt
		try {
			FileWriter fw = new FileWriter("groups.txt", true);
			fw.write(groupName + ":" + admin + "\n");
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// cria directoria groups caso nao exista previamente
		PersistenceUtil.createDir("groups");

		// cria group
		long conversationId = conversationFactory.generateID();
		Group novoGrupo = new Group(groupName, admin, conversationId);

		// Persiste grupo na directoria groups
		PersistenceUtil.createDir("groups/" + groupName);
		PersistenceUtil.writeObject(novoGrupo, "groups/" + groupName + "/group");

		// Cria directoria da conversaçao com o respectivo id na pasta de
		// conversacoes
		PersistenceUtil.createDir("conversations/" + conversationId);
		PersistenceUtil.createDir("conversations/" + conversationId + "/messages");
		PersistenceUtil.createFile("conversations/" + conversationId + "/conversation");

		return conversationId;
	}

	/**
	 * Elimina o registo de um grupo em ficheiro
	 * 
	 * @param groupName
	 *            Nome do grupo a ser eliminado do ficheiro 
	 */
	public void deleteGroup(String groupName) {
		// elimina entrada no ficheiro groups.txt
		File file = new File("groups.txt");
		File tempFile = new File("tempGroups.txt");

		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

			String line;

			// copiar todos os users para o ficheiro temp
			while ((line = reader.readLine()) != null) {
				String currentUsername = line.split(":")[0];

				// nao copiar user
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

		// elimina ficheiro antigo
		if (!file.delete()) {
			System.out.println("Could not delete file");
			return;
		}
		if (!tempFile.renameTo(file))
			System.out.println("Could not rename file");

		// eliminar pasta do grupo
		file = new File("groups/" + groupName);
		PersistenceUtil.delete(file);
	}
}
