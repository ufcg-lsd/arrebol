package main

import (
	"encoding/json"
	"github.com/gorilla/mux"
	"github.com/satori/go.uuid"
	"io/ioutil"
	"log"
	"net/http"
)

type JobSpec struct {
	Label string `json:"label"`
}

type Job struct {

	//TODO: add a JobSpec directly
	Id    string `json:"id"`
	Label string `json:"label"`
}

var Jobs []Job

func GetVersion(w http.ResponseWriter, r *http.Request) {
	json.NewEncoder(w).Encode("1.0")
}

func CreateJob(w http.ResponseWriter, r *http.Request) {

	reqBody, _ := ioutil.ReadAll(r.Body)

	var newSpec JobSpec
	json.Unmarshal(reqBody, &newSpec)

	newJobId := uuid.Must(uuid.NewV4()).String()
	var newJob Job = Job{Id: newJobId, Label: newSpec.Label}

	Jobs = append(Jobs, newJob)

	//response
	w.WriteHeader(http.StatusCreated)
	json.NewEncoder(w).Encode(newJob.Id)
}

func GetJob(w http.ResponseWriter, r *http.Request) {

	//TODO: what if there is no job for job_id?

	vars := mux.Vars(r)
	job_id := vars["id"]

	for _, job := range Jobs {
		if job.Id == job_id {
			w.WriteHeader(http.StatusOK)
			json.NewEncoder(w).Encode(job)
		}
	}
}

func main() {

	router := mux.NewRouter()

	router.HandleFunc("/version", GetVersion).Methods("GET")
	router.HandleFunc("/job", CreateJob).Methods("POST")
	router.HandleFunc("/job/{id}", GetJob).Methods("GET")

	log.Fatal(http.ListenAndServe(":8080", router))
}
