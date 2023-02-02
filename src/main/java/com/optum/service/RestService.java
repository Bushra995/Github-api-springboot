package com.optum.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.optum.config.AuthHeaderInterceptor;
import com.optum.repository.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class RestService {
	private static final Logger LOGGER = Logger.getLogger(RestService.class.getName());

	//@Bean
	//object flro ecl genrtaor


	//If in your GitHub Enterprise instance the repositories are associated with organizations instead of individual users, then you should use the organizations endpoint instead of users endpoint. So the url should be
	private static final String username = "Brehman";
	//String url = "https://api.github.com/users";
	String url = "https://github.optum.com/api/v3/organizations/Brehman";
	//search url
	private final String search_url = "https://github.optum.com/api/v3/search/code";
	private static final String baseUrl = "https://github.optum.com/api/v3/orgs/";

	//String token = "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";
	// String token ="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";

	@Value("${token}")//accessing value from application.properties
	String token = "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";
	//public static final String authorization = "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";
//public static final String authorization="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";
	public static final String authorization = "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";
	//maikng object for excel report
	private final RestTemplate restTemplate;


	@Autowired
	public RestService(AuthHeaderInterceptor authHeaderInterceptor) {
		this.restTemplate = new RestTemplate();
		List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
		interceptors.add(authHeaderInterceptor);
		restTemplate.setInterceptors(interceptors);
		//restTemplate.getInterceptors(Collections.singletonList(new AuthHeaderInterceptor(token)));
	}

	HttpHeaders Header = new HttpHeaders();
	//HttpHeaders headers = new HttpHeaders();

	@Bean
	RestOperations restTemplateBuilder(RestTemplateBuilder restTemplateBuilder) {
		return restTemplateBuilder.basicAuthentication("username", "token").build();
	}

	//public final RestTemplate restTemplate;

	// create client
	//authprization using header


	//public repo controller

	HttpClient client = HttpClient.newHttpClient();

	// create request
	HttpRequest request = HttpRequest.newBuilder()
			.uri(URI.create("https://github.optum.com/api/v3/repos/:owner/:repo/commits"))
			.header("Authorization", token)
			.build();


	//private static final String authorization = "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";
	//private static final String baseUrl = "https://api.github.com/users/"+username;

	private static final ObjectMapper objectMapper = new ObjectMapper();

	//get request
	public String getrepo(String org) throws IOException, InterruptedException {
		url = baseUrl + org + "/repos?affiliation=organization_member";
		var request = HttpRequest.newBuilder().uri(URI.create(url))
				.setHeader("Authorization", authorization)
				.GET()
				.build();

		var response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
		return response.body();
	}

	//get http entit so i won't have to crate again
	public HttpEntity<String> getentity() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Token " + token);
		HttpEntity<String> entity = new HttpEntity<>(headers);
		return entity;
	}

	public List<String> getRepoNames(String organization) {
		String url = baseUrl + organization + "/repos?affiliation=organization_member";
		//	String url = "https://github.optum.com/api/v3/orgs/" + organization + "/repos";
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Token " + token);
		HttpEntity<String> entity = new HttpEntity<>(headers);
		Repository[] repos = restTemplate.getForObject(url, Repository[].class, getentity());
		List<String> copy = Arrays.stream(repos).map(Repository::getName).collect(Collectors.toList());
		List<Boolean> readme= new ArrayList<>();
		for(String repo:copy){
			readme.add(readmeFileExists(organization,repo));
		}
	//	List<Boolean> readme = readmeFileExists(organization,copy);
		//List<Boolean> readme = Arrays.stream(repos).map(Repository::isReadmeExists).collect(Collectors.toList());
		try {
			reposToExcel(organization, copy, readme);
		} catch (Exception e) {
			return Collections.singletonList(e + " error");
		}
		return copy;

	}

	public boolean readmeFileExists(String organization, String repo) {

		String url = "https://github.optum.com/api/v3/repos/" + organization + "/" + repo + "/readme";
		try {
			restTemplate.getForObject(url, Object.class, getentity());
			return true;
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
				return false;
			} else {
				throw e;
			}
		}
	}


	//list of repos in type object of repositor and check if it has readme or not

	public List<Repository> getRepositories(String username) {

		String url = baseUrl + username + "/repos";
		Repository[] repositories = restTemplate.getForObject(url, Repository[].class, getentity());

		return Arrays.asList(repositories);
	}


	//genrate excel report
	public void reposToExcel(String org_name, List<String> repositoryList, List<Boolean> repo_readme) throws IOException {
		// RestService serviceobj= new RestService();
		String filepath = "src/main/resources/dataFiles/Repos"+ org_name + "report.xlsx";
		filepath = filepath.replace("\n", "");

		String[] columns_Header = {"Repo name", "readme exist"};
		try (
				Workbook workbook = new XSSFWorkbook();
				FileOutputStream outstream = new FileOutputStream(filepath);
				//  ByteArrayOutputStream outstream= new ByteArrayOutputStream();
		) {
			Sheet sheet = workbook.createSheet("Repos");

			Font headerFont = workbook.createFont();
			headerFont.setBold(true);
			headerFont.setColor(IndexedColors.BLUE.getIndex());

			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFont(headerFont);

			//row for header
			Row headerrow = sheet.createRow(0);
			//headet:loop becuae header r composed in areY
			for (int col = 0; col < columns_Header.length; col++) {
				Cell cell = headerrow.createCell(col);
				cell.setCellValue(columns_Header[col]); //array[0] a[1]
				cell.setCellStyle(headerCellStyle);
			}

			int rowIDX = 1;
			//saving from getall user end pint to othery list
			List<String> b = new ArrayList<>();
			//b.addAll(getRepoNames(username));
			for (int i = 0; i < repositoryList.size(); i++) {
				Row row = sheet.createRow(rowIDX++);

				//get repo name

				//row.createCell(0).setCellValue(repositoryList.get(i));
				row.createCell(0).setCellValue(repositoryList.get(i));
				row.createCell(1).setCellValue(readmeFileExists(username, repositoryList.get(i)));
				//row.createCell(1).setCellValue(readmeFileExists("Bushra995",repositoryList.get(i)));
			}

			//  String filepath="C:\\Users\\brehman\\Documents\\workspace-spring-tool-suite-4-4.3.0.RELEASE\\Git-user-api\\src\\main\\resources\\dataFiles\\Repos.xlsx";
			// FileOutputStream outstream =new FileOutputStream(filepath);
			workbook.write(outstream);
			workbook.close();
			// return  new ByteArrayInputStream(outstream);
			System.out.println("Employe file: "+filepath+" written succesfully !");

		}


	}

	//search for a file name i workbook only /-
	public String searchName(String name) throws IOException {
		String filepath = "src/main/resources/dataFiles/Repos.xlsx";
		int row1 = 0;
		int column1 = 0;
		// Open the spreadsheet
		try {
			FileInputStream file = new FileInputStream(new File(filepath));

			XSSFWorkbook workbook = new XSSFWorkbook(file);
			// Get the first sheet
			XSSFSheet sheet = workbook.getSheetAt(0);


			for (Row row : sheet) {
				for (Cell cell : row) {
					if (cell.getCellType() == CellType.STRING) {
						//if (cell.getStringCellValue().contains(equalsIgnoreCase(name)))
						if (cell.getStringCellValue().toLowerCase().contains(name.toLowerCase())) {
							// Your logic to handle the matched name
							row1 = cell.getRowIndex();
							column1 = cell.getColumnIndex();
							return name + " exist on row " + cell.getRowIndex() + " and column : " + cell.getColumnIndex();
						}
					}
				}
			}

			workbook.close();
			file.close();
		} //end of try
		catch (Exception e) {

			return "No such name";
		}

		if (row1 == 0 && column1 == 0) {
			return "Not Exist";
		} else {
			return name + " exist on row " + row1 + " and column : " + column1;
		}

	}


	//to check if a specific file exist or not :
	public boolean anyFileExists(String organization, String repo, String filepath) {
		//String url = "https://api.github.com/repos/" + organization + "/" + repo + "contents/"+ filepath;
		String url = baseUrl + organization + "/" + repo + "/contents/" + filepath;
		try {
			restTemplate.getForObject(url, Object.class, getentity());
			return true;
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
				return false;
			} else {
				throw e;
			}
		}
	}


	//serach for a file in repo github
	public String SearchFile(String username, String repo_name, String file_name) {
		String query = "filename:" + file_name + "+repo:" + username + "/" + repo_name;
		String url = search_url + "?q=" + query;
		RestTemplate restTemplate = new RestTemplate();
		//Here you can add the Auth token to the
		//request header
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Token " + token);
		HttpEntity<String> entity = new HttpEntity<>(headers);
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);


		String result = response.getBody();


		return result;

	}


	//get list of all directory in a repo

	//get list of all directory in an repo in github organisation using springboot rest api


	public List<String> getDirectoryinRepos(String organization, String repo_name) {
		String url = "https://github.optum.com/api/v3/" + "repos/" + organization + "/" + repo_name + "/contents";
		//String url = "https://api.github.com/repos/" + organization +"/"+ repo_name+"/contents";
		Directory[] direc = restTemplate.getForObject(url, Directory[].class);
		List<String> direc_name = Arrays.stream(direc).map(Directory::getName).collect(Collectors.toList());
		//List<Boolean> readme = Arrays.stream(repos).map(Repository::isReadmeExists).collect(Collectors.toList());
//		try {
//			reposToExcel(direc_name, readme);
//		} catch (Exception e) {
//			return Collections.singletonList(e + " error");
//		}
		return direc_name;

	}


	//get list of files in directory
	public List<String> getFilesInDirectory(String organization, String repo, String directory) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		HttpEntity<String> entity = new HttpEntity<>(headers);
		String url = baseUrl + "repos/" + organization + "/" + repo + "/git/trees/" + directory;
		//String url = "https://api.github.com/repos/" + organization + "/" + repo + "/git/trees/" + directory;

		ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

		List<Map<String, Object>> files = (List<Map<String, Object>>) response.getBody().get("tree");
		List<String> fileNames = new ArrayList<>();
		for (Map<String, Object> file : files) {
			fileNames.add((String) file.get("path"));
		}
		return fileNames;
	}


	//trees
	//get files in a direcg
	public List<String> getFiles(String organization, String repo_name, String direc) {
		String url = "https://github.optum.com/api/v3/" + "repos/" + organization + "/" + repo_name + "/contents" + direc;
		//String url = "https://api.github.com/repos/" + organization +"/"+ repo_name+"/contents"+direc;
		Files[] files_list = restTemplate.getForObject(url, Files[].class);
		List<String> file_name = Arrays.stream(files_list).map(Files::getName).collect(Collectors.toList());


		return file_name;
		//Directory[] direc = restTemplate.getForObject(url, Directory[].class);
	}

//get directory structure

	public List<Content> getRepoStructure(String owner, String repo) throws IOException {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.set("Authorization", "Token " + "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");
			HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
			String url = "https://github.optum.com/api/v3/repos/" + owner + "/" + repo + "/git/trees/master?recursive=1";
			// https://github.optum.com/api/v3/repos/Cosmos-dev/idt-auth-poc/git/trees/master?recursive=1
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
			String json = response.getBody();
			JsonNode jsonNode = objectMapper.readTree(json);

			if (jsonNode.has("message")) {
				//log.error("Error retrieving repository structure: " + jsonNode.get("message").asText());
				return Collections.emptyList();
			}

			JsonNode tree = jsonNode.get("tree");
			if (tree == null) {
				//log.error("Error retrieving repository structure: tree node not found in API response");
				return Collections.emptyList();
			}

			List<Content> contents = new ArrayList<>();
			for (JsonNode t : tree) {
				Content content = objectMapper.treeToValue(t, Content.class);
				contents.add(content);
			}
			return contents;
		} catch (Exception e) {
			//log.error("Error occurred while fetching repository structure", e);
			throw new IOException("Error occurred while fetching repository structure"+e);
		}
	}


	//make file available using url
//	@GetMapping("/download/{organization}")
	public ResponseEntity<String> downloadExcel(String organization) {
		//String filepath = "src/main/resources/dataFiles/" + organization + ".xlsx";
		String filepath = "src/main/resources/dataFiles/Repos-" + organization + "-report.xlsx";
		File file = new File(filepath);
		if (file.exists()) {
			URI fileUri = file.toURI();
			return ResponseEntity.ok().body(fileUri.toString());
		} else {
			getRepoNames(organization);
			file = new File(filepath);
			URI fileUri = file.toURI();
			return ResponseEntity.ok().body(fileUri.toString());
		}
	}

	//asearch for a file //
	//q-1 >search for a file present inside an org and if present so retun its path using github api java springo
	public String getfileinOrg(String org_name, String file_name) {
		List<String> filePaths = new ArrayList<>();

		try {
			String query = "filename:" + file_name + "+org:" + org_name;
			//	String query = "filename:"+file_name + "+in:path+org:" + org_name;
			String url = search_url + "?q=" + query;


			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getentity(), String.class);

			// parse the response and extract the file paths

			if (response.getStatusCode().is2xxSuccessful()) {
				return response.getBody();
			} else {
				// Handle error
				throw new RuntimeException("Error searching for file: " + response.getStatusCode());
			}
		} catch (Exception e) {
			return e.getMessage();
		}
	}


	public String searchFileByName(String organization, String fileName) {
		String query = "https://github.optum.com/api/v3/search/code?q=org:" + organization + "+filename:" + fileName;
		//	String query = "org:" + organization+"+filename:" + fileName ;
	//	String url = search_url + "?q=" + query;
		ResponseEntity<GHCodeSearchResult> response = restTemplate.exchange(query, HttpMethod.GET, getentity(), GHCodeSearchResult.class);



		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Token " + token);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
		String url = "https://github.optum.com/api/v3/search/code?q=org:" + organization+ "+filename:" + fileName;
		ResponseEntity<String> responsee= restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
		return responsee.getBody();
	}

	//serch for a file inn org
	public Map<String, String> search(String org, String filename) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Token " + "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");

		HttpEntity<String> entity = new HttpEntity<>(headers);

		RestTemplate restTemplate = new RestTemplate();
//https://github.optum.com/api/v3/search/code?q=org:cosmos-dev+filename:application-local.properties
		String query = "org:" + org + "+filename:" + filename;
		String urll = search_url + "?q=" + query;
		String url = "https://github.optum.com/api/v3/search/code?q=org:" + org + "+filename:" + filename;
		GHCodeSearchResult ghCodeSearchResult = restTemplate.exchange(url, HttpMethod.GET, entity, GHCodeSearchResult.class).getBody();

		//total count : total times file repeated
		int totalCount = ghCodeSearchResult.getTotalCount();
		System.out.println("Total Count: " + totalCount);

	        //GHCodeSearchResult ghCodeSearchResult = restTemplate.exchange(url, HttpMethod.GET,entity,GHCodeSearchResult.class);
		//List<Item> items = new ArrayList<>();
		List<Item> items = ghCodeSearchResult.getItems();
			ArrayList<String> repoNames = new ArrayList<>();
		    ArrayList<String> Urls = new ArrayList<>();
			ArrayList<String> htmlUrls= new ArrayList<>();
		for (Item item : items) {
			repoNames.add(item.getRepository().getName());
			Urls.add(item.getRepository().getUrl());
			if (!item.getHtmlUrl().isEmpty()) {
				htmlUrls.add(item.getHtmlUrl());
			}

		}


		Map<String,String> map = new HashMap<>();
		for (Item item : items) {
			map.put(item.getRepository().getName(), item.getHtmlUrl());
		}


		//return response.getBody();
		return map;

	}

	//search for a text in a filr
     public Map<String, String> searchText(String org, String Text){
		//authorization

		 HttpHeaders headers = new HttpHeaders();
		 headers.set("Authorization", "Token " + "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");

		 HttpEntity<String> entity = new HttpEntity<>(headers);

		 RestTemplate restTemplate = new RestTemplate();
		 String query="";
		 String url="https://github.optum.com/api/v3/search/code?q="+Text+"+in:file+org:"+org;
		 //https://github.optum.com/api/v3/search/code?q= container_name: test-cosmosidt-angular-os+in:file+org:Cosmos-dev
		 //https://github.optum.com/api/v3/search/code?q= container_name: test-cosmosidt-angular-os+in:file+orgCosmos-dev
		 GHCodeSearchResult ghCodeSearchResult = restTemplate.exchange(url, HttpMethod.GET, entity, GHCodeSearchResult.class).getBody();
           System.out.println(ghCodeSearchResult);
		   System.out.println("query: "+url);
		   
		 //total count : total times file repeated
		 int totalCount = ghCodeSearchResult.getTotalCount();
		 System.out.println("Total Count: " + totalCount);

		 //atrong item nMES IN ITEM CLASS type
		 List<Item> items = ghCodeSearchResult.getItems();
		 ArrayList<String> filename= new ArrayList<>();
		 ArrayList<String> repoNames = new ArrayList<>();
		 ArrayList<String> Urls = new ArrayList<>();
		 ArrayList<String> htmlUrls= new ArrayList<>();
		 //iterating through each object json object and adding intp list of repo names of type reponame and adding url to urllist
		 for (Item item : items) {
			 filename.add(item.getName());
			 Urls.add(item.getHtmlUrl());
			 repoNames.add(item.getRepository().getName());
//			 repoNames.add(item.getRepository().getName());
//			 Urls.add(item.getRepository().getUrl());
			 if (!item.getHtmlUrl().isEmpty()) {
				 htmlUrls.add(item.getHtmlUrl());
			 }

		 }

//now stroing both url and name in form of map
		 Map<String,String> map = new HashMap<>();
		 for (Item item : items) {
			 map.put(item.getName(), item.getHtmlUrl());
		 }


		 return map;
	 }
//get directory structure



}

