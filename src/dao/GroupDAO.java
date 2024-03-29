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
import exceptions.InvalidMacException;
import factories.ConversationFactory;
import util.PersistenceUtil;
import util.SecurityUtils;

/**
 * Classe que gere os grupos, persistindo-os em disco
 * 
 * @author António, José, Pedro
 */
public class GroupDAO {

	private static ConversationFactory conversationFactory;
	
	private String serverPassword;

	public GroupDAO(String serverPassword) {
		this.serverPassword = serverPassword;
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
	 * @throws InvalidMacException 
	 * @requires username != null && groupName != null
	 */
	public boolean addUserToGroup(String username, String groupName) throws InvalidMacException {
		String filePath = "groups/" + groupName + "/group";

		// validar ficheiro MAC
		SecurityUtils.validateFileMac("groups.txt", serverPassword);
		
		Group group = (Group) PersistenceUtil.readObject(filePath);
		
		System.out.println("[GroupDAO] addUserToGroup: " + group.getUsers());

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
	 * @throws InvalidMacException 
	 */
	public ConcurrentHashMap<String, String> getGroups() throws InvalidMacException {
		ConcurrentHashMap<String, String> groups = new ConcurrentHashMap<String, String>();

		String line;
		String filePath = "groups.txt";
		BufferedReader br;

		// criar ficheiro caso este nao exista
		PersistenceUtil.createFile(filePath);
		File file = new File(filePath);
		
		// validar ficheiro MAC
		SecurityUtils.validateFileMac(filePath, serverPassword);

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

				br.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return groups;
	}

	/**
	 * Cria um grupo
	 * 
	 * @param groupName
	 *            Nome do grupo a ser criado
	 * @param admin
	 *            Dono do grupo, isto é quem cria o grupo
	 * @return Devolve o id da conversação associada ao grupo
	 * @throws InvalidMacException 
	 * @requires groupName != null && admin != null
	 */
	public Long createGroup(String groupName, String admin) throws InvalidMacException {
		
		String filePath = "groups.txt";

		// validar ficheiro MAC
		SecurityUtils.validateFileMac(filePath, serverPassword);

		// adiciona uma entrada no ficheiro groups.txt
		try {
			FileWriter fw = new FileWriter(filePath, true);
			fw.write(groupName + ":" + admin + "\n");
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// atualizar ficheiro MAC
		SecurityUtils.updateFileMac(filePath, serverPassword);

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
	 * @throws InvalidMacException 
	 * @requires groupName != null
	 */
	public void deleteGroup(String groupName) throws InvalidMacException {
		String filePath = "groups.txt";

		// validar ficheiro MAC
		SecurityUtils.validateFileMac(filePath, serverPassword);
		
		// elimina entrada no ficheiro groups.txt
		File file = new File(filePath);
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
			System.out.println("Erro ao eliminar ficheiro");
			return;
		}
		if (!tempFile.renameTo(file))
			System.out.println("Erro ao renomerar ficheiro");

		// eliminar pasta do grupo
		file = new File("groups/" + groupName);
		PersistenceUtil.delete(file);
		
		// atualizar ficheiro MAC
		SecurityUtils.updateFileMac(filePath, serverPassword);
	}

	/**
	 * Obtem um grupo do ficheiro guardado em disco
	 * 
	 * @param groupName
	 *            Nome do grupo a obter
	 * @return Group chamado groupNAme ou null caso nao exista esse grupo
	 * @requires groupName != null
	 */
	public Group getGroup(String groupName) {
		String filePath = "groups/" + groupName + "/group";
		Group group = (Group) PersistenceUtil.readObject(filePath);

		return group;
	}

	/**
	 * Remove um user do Group e guarda essa informação em disco
	 * 
	 * @param group
	 *            Nome do grupo
	 * @param userToRemove
	 *            Nome do utilizador a ser removido
	 * @requires group != null && userToRemove != null
	 */
	public void removeUserFromGroup(Group group, String userToRemove) {
		group.removeMember(userToRemove);
		PersistenceUtil.writeObject(group, "groups/" + group.getName() + "/group");
	}
}
