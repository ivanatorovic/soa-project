package main

import (
	"log"
	"net/http"
	"net/http/httputil"
	"net/url"
	"os"
	"strings"
)

func newProxy(target string) *httputil.ReverseProxy {
	if target == "" {
		log.Fatal("Target URL nije postavljen")
	}

	parsedURL, err := url.Parse(target)
	if err != nil {
		log.Fatal("Greška pri parsiranju URL-a:", err)
	}

	proxy := httputil.NewSingleHostReverseProxy(parsedURL)

	proxy.ErrorHandler = func(w http.ResponseWriter, r *http.Request, err error) {
		log.Println("Proxy greška:", err)
		http.Error(w, "Servis trenutno nije dostupan", http.StatusBadGateway)
	}

	return proxy
}

func getEnv(key, fallback string) string {
	value := os.Getenv(key)
	if value == "" {
		return fallback
	}
	return value
}

func main() {
	stakeholdersURL := getEnv("STAKEHOLDERS_SERVICE_URL", "http://localhost:8081")
	blogURL := getEnv("BLOG_SERVICE_URL", "http://localhost:8082")
	toursURL := getEnv("TOURS_SERVICE_URL", "http://localhost:8083")
	followerURL := getEnv("FOLLOWER_SERVICE_URL", "http://localhost:5000")
	port := getEnv("PORT", "8000")

	stakeholdersProxy := newProxy(stakeholdersURL)
	blogProxy := newProxy(blogURL)
	toursProxy := newProxy(toursURL)
	followerProxy := newProxy(followerURL)

	http.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {
		log.Println("Request:", r.Method, r.URL.Path)

		if r.URL.Path == "/" {
			w.Write([]byte("API Gateway radi"))
			return
		}

		if strings.HasPrefix(r.URL.Path, "/api/auth") {
			stakeholdersProxy.ServeHTTP(w, r)
			return
		}

		if strings.HasPrefix(r.URL.Path, "/api/users") {
			stakeholdersProxy.ServeHTTP(w, r)
			return
		}

		if strings.HasPrefix(r.URL.Path, "/profile-images/") {
			stakeholdersProxy.ServeHTTP(w, r)
			return
		}

		if strings.HasPrefix(r.URL.Path, "/api/blog") {
			blogProxy.ServeHTTP(w, r)
			return
		}

		if strings.HasPrefix(r.URL.Path, "/api/comments") {
			blogProxy.ServeHTTP(w, r)
			return
		}

		if strings.HasPrefix(r.URL.Path, "/api/tours") {
			toursProxy.ServeHTTP(w, r)
			return
		}

		if strings.HasPrefix(r.URL.Path, "/api/follows") {
			followerProxy.ServeHTTP(w, r)
			return
		}

		http.NotFound(w, r)
	})

	log.Println("API Gateway pokrenut na portu", port)
	err := http.ListenAndServe(":"+port, nil)
	if err != nil {
		log.Fatal("Greška pri pokretanju servera:", err)
	}
}
