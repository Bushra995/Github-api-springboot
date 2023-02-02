package com.optum.controller;

import com.optum.repository.Content;
import com.optum.repository.Repository;
import com.optum.service.RestService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController

public class RestControllerr {
	//@Autowired
	public final RestService serviceobj;


	@Autowired //making object for both srrvice end pinta nd excel genrator

	public RestControllerr(RestService repoService ) {
		this.serviceobj = repoService;

	}


	String GITHUB_API_BASE_URL = "https://api.github.com";
	String orgName = "Bushra995";
	String token = "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";


	@GetMapping("/")
	public String hello() {
		return "hello";
	}



	//list repos in org and genertae excel report
	@GetMapping("/listrepo/{org_name}")
	public List<String> getRepoNames(@PathVariable("org_name") String orgName) {

		return serviceobj.getRepoNames(orgName);
		//COSMOS-ESEG
	}



//get list of repos in json format

	@GetMapping("/reposs/{org_name}")
	public List<Repository> getRepositories(@PathVariable("org_name") String org_name) {
		String url = "https://api.github.com/orgs/" + org_name+ "/repos";
		return serviceobj.getRepositories(org_name);
	}

	//@GetMapping("/download")


//get workbook

	@GetMapping("/workbook")
	public ResponseEntity<List<List<Object>>> getWorkbookContents(HttpServletRequest request) throws IOException {
		String realPath = request.getServletContext().getRealPath("C:\\Users\\brehman\\Documents\\Repos.xlsx");
		FileInputStream fis = new FileInputStream(realPath);

		Workbook workbook = new XSSFWorkbook(fis);
		Sheet sheet = workbook.getSheetAt(0);

		List<List<Object>> rows = new ArrayList<>();
		for (Row row : sheet) {
			List<Object> cells = new ArrayList<>();
			for (Cell cell : row) {
				cells.add(cell.getStringCellValue());
			}
			rows.add(cells);
		}

		fis.close();
		return new ResponseEntity<>(rows, HttpStatus.OK);
	}

	@GetMapping("/searchname/{name}")
	public  String searchName(@PathVariable("name")  String name){

		try{
		return serviceobj.searchName(name);}
		catch (Exception e){
			return "Error";
		}
	}

//chek for a file in repo
   //@GetMapping("/file/{repo_name}/{path}")
	@GetMapping("/file/{org_name}/{repo_name}/{path}/**")
   public  boolean searchfile(@PathVariable("org_name") String org_name, @PathVariable("repo_name")  String repo_name ,  @PathVariable("path") String path){

		File file = new File(path);
	  // try{}
		   String filapath="src/main/java/com/optum/controller/UserController";
		   return serviceobj.anyFileExists(org_name,repo_name,path);



   }

//check if readme exist or not
	@GetMapping("/readme/{org_name}/{repo_name}")
	public boolean ReadmeFileExist(@PathVariable("org_name")String org_name, @PathVariable("repo_name") String repo_name) {
		return serviceobj.readmeFileExists(org_name, repo_name);
	}

	//search for a file
	@GetMapping("/Search/{org_name}/{repo_name}/{file_name}")
	public  String SearchFile( @PathVariable("org_name") String org_name,@PathVariable("repo_name") String repo_name,@PathVariable("file_name") String file_name){
		return serviceobj.SearchFile(org_name, repo_name,file_name);
	}

	//get contents in a repo
	@GetMapping("/Contents/{org_name}/{repo_name}")
	public List<String> GetContents(@PathVariable("org_name") String org_name,@PathVariable("repo_name") String repo_name){
		return serviceobj.getDirectoryinRepos(org_name,repo_name);
	}

	//GET ALL files in directory in a repos

@GetMapping("/Files/{org_name}/{repo_name}/{directory}")
	public List<String> getfilesindirectory(@PathVariable("org_name") String org_name, @PathVariable("repo_name") String repo_name, @PathVariable("directory") String directory){
		//return serviceobj.getTree("Bushra995",repo_name,directory);
	return serviceobj.getFiles(org_name,repo_name,directory);
}

//get list of all files name in a directory :structure
	@GetMapping("/Content/{owner_name}/{repo_name}")
	public List<Content> getRepoStructure(@PathVariable String owner_name, @PathVariable String repo_name){
		try{
		return serviceobj.getRepoStructure(owner_name,repo_name);
		}
		catch (Exception e){
			System.out.println("error "+e);
			return null;


		}
	}

	@GetMapping("/repos")
	public String repos(){
		try{
		return  serviceobj.getrepo("cosmos-dev");
		}
		catch (Exception e){
			return e.getMessage();
		}
	}
	//diwnload excel
	@GetMapping("/download/{organization}")
	ResponseEntity<String> downloadExcel( @PathVariable String organization){
		return serviceobj.downloadExcel(organization);
	}
	//get file in org
	@GetMapping("/file/{org}/{file_to_found}")
	Map<String,String> getFileInorg(@PathVariable("org") String org, @PathVariable("file_to_found") String file_to_found){
		return serviceobj.search(org,file_to_found);

	}

	@GetMapping("/Search")
	public Map<String, String> searchText(@RequestParam("org") String org, @RequestParam("text") String text){

		return serviceobj.searchText(org,text);
	}


}
