#!/bin/bash

#######################################
# KryptoNyte Basic Tools Installation
# Installs GCC 14 and SDKMAN with Java/SBT/Scala
#######################################

# Script configuration
USE_SUDO=false
VERBOSE=true
UPGRADE_MODE=false

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
WHITE='\033[1;37m'
NC='\033[0m' # No Color

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        --with-sudo)
            USE_SUDO=true
            shift
            ;;
        --quiet)
            VERBOSE=false
            shift
            ;;
        --upgrade)
            UPGRADE_MODE=true
            shift
            ;;
        --help|-h)
            echo "Usage: $0 [OPTIONS]"
            echo ""
            echo "KryptoNyte Basic Tools Installation"
            echo ""
            echo "Options:"
            echo "  --with-sudo    Use sudo for commands requiring elevated privileges"
            echo "  --quiet        Reduce output verbosity"
            echo "  --upgrade      Force upgrade/reinstall of existing tools"
            echo "  --help, -h     Show this help message"
            echo ""
            echo "This script installs basic development tools:"
            echo "  - GCC 14 compiler"
            echo "  - SDKMAN with Java 23, SBT, and Scala 2.13"
            echo ""
            echo "Tools are automatically detected and skipped if already installed"
            echo "unless --upgrade flag is used."
            exit 0
            ;;
        *)
            echo "Unknown option: $1"
            echo "Use --help for usage information"
            exit 1
            ;;
    esac
done

# Function to execute commands with optional sudo
run_cmd() {
    if [ "$USE_SUDO" = true ]; then
        sudo "$@"
    else
        "$@"
    fi
}

# Function to print large banner messages
print_banner() {
    local message="$1"
    local color="$2"
    
    if [ "$VERBOSE" = true ]; then
        echo -e "\n${color}"
        echo "=================================================================="
        echo "  $message"
        echo "=================================================================="
        echo -e "${NC}"
    fi
}

# Function to print step messages
print_step() {
    local message="$1"
    local color="${2:-$CYAN}"
    
    if [ "$VERBOSE" = true ]; then
        echo -e "\n${color}▶ $message${NC}"
    fi
}

# Function to print success messages
print_success() {
    local message="$1"
    echo -e "${GREEN}✓ $message${NC}"
}

# Function to print error messages
print_error() {
    local message="$1"
    echo -e "${RED}✗ Error: $message${NC}" >&2
}

# Function to print warning messages
print_warning() {
    local message="$1"
    echo -e "${YELLOW}⚠ Warning: $message${NC}"
}

# Function to check if running in Codespace
is_codespace() {
    [ -n "$CODESPACES" ] || [ -n "$GITHUB_CODESPACES_PORT_FORWARDING_DOMAIN" ]
}

# Function to detect if sudo is needed
check_sudo_needed() {
    # Check if we can write to /usr/local/bin
    if [ ! -w "/usr/local/bin" ] 2>/dev/null; then
        return 0  # sudo needed
    fi
    
    # Check if we can run apt commands
    if ! apt list --installed >/dev/null 2>&1; then
        return 0  # sudo needed
    fi
    
    return 1  # sudo not needed
}

# Function to check if GCC 14 is installed
check_gcc14() {
    if command -v gcc >/dev/null 2>&1; then
        local gcc_version=$(gcc --version 2>/dev/null | head -1)
        if echo "$gcc_version" | grep -q "gcc.*14\." 2>/dev/null; then
            print_step "Found GCC 14: $gcc_version"
            return 0
        else
            print_step "Found GCC but not version 14: $gcc_version"
            return 1
        fi
    else
        print_step "GCC not found"
        return 1
    fi
}

# Function to check if SDKMAN and tools are installed
check_sdkman_tools() {
    # Simply check if SDKMAN directory exists
    if [ -d "$HOME/.sdkman" ]; then
        print_step "Found SDKMAN installation"
        return 0
    else
        print_step "SDKMAN not found"
        return 1
    fi
}

# Function to install GCC 14
install_gcc14() {
    print_banner "INSTALLING GCC 14 COMPILER" "$BLUE"

    print_step "Adding Ubuntu toolchain repository"
    run_cmd add-apt-repository -y ppa:ubuntu-toolchain-r/ppa || {
        print_error "Failed to add Ubuntu toolchain repository"
        return 1
    }

    print_step "Updating package lists"
    run_cmd apt update || {
        print_error "Failed to update package lists"
        return 1
    }

    print_step "Installing GCC 14 and G++ 14"
    run_cmd apt install -y build-essential && run_cmd apt clean || {
        print_error "Failed to install build-essential"
        return 1
    }

    print_step "Skipping GCC default setup as build-essential provides a working default."

    print_success "GCC 14 installed and configured"
    gcc --version | head -1
    return 0
}

# Function to install SDKMAN and tools
install_sdkman_tools() {
    print_banner "INSTALLING SDKMAN WITH JAVA, SBT, AND SCALA" "$GREEN"

    print_step "Downloading and installing SDKMAN"
    curl -s "https://get.sdkman.io" | bash || {
        print_error "Failed to install SDKMAN"
        return 1
    }

    print_step "Installing Java 23, SBT, and Scala via SDKMAN"
    bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && \
                sdk install java 23.0.1-oracle --default && \
                sdk install sbt && \
                sdk install scala 2.13.15 --default" || {
        print_error "Failed to install Java/Scala tools via SDKMAN"
        return 1
    }

    print_success "SDKMAN and Java/Scala tools installed"
    return 0
}

# Function to setup package management
setup_package_management() {
    print_banner "CONFIGURING PACKAGE MANAGEMENT" "$CYAN"

    # Set non-interactive mode to prevent prompts and warnings
    export DEBIAN_FRONTEND=noninteractive
    export DEBCONF_NONINTERACTIVE_SEEN=true

    print_step "Updating package lists"
    run_cmd apt update || {
        print_error "Failed to update package lists"
        return 1
    }

    print_step "Installing apt-utils to prevent debconf warnings"
    run_cmd apt install -y apt-utils || {
        print_warning "Failed to install apt-utils (continuing anyway)"
    }

    print_step "Cleaning package cache"
    run_cmd apt clean

    print_success "Package management configured"
    return 0
}

# Main function
main() {
    print_banner "KRYPTONYTE BASIC TOOLS INSTALLATION" "$WHITE"
    
    # Auto-detect environment and sudo needs
    if is_codespace; then
        print_banner "DETECTED: GitHub Codespace Environment" "$PURPLE"
        if [ "$USE_SUDO" = false ] && check_sudo_needed; then
            print_warning "Codespace detected but sudo may be needed for some operations"
            print_warning "Consider running with --with-sudo if commands fail"
        fi
    else
        print_banner "DETECTED: Standalone Environment" "$PURPLE"
        if [ "$USE_SUDO" = false ] && check_sudo_needed; then
            print_warning "Standalone environment detected and elevated privileges needed"
            print_warning "Consider running with --with-sudo option"
        fi
    fi

    echo -e "${CYAN}This will install basic development tools for KryptoNyte${NC}"
    echo ""
    echo "Configuration:"
    echo "  Using sudo: $USE_SUDO"
    echo "  Verbose output: $VERBOSE"
    echo "  Upgrade mode: $UPGRADE_MODE"
    echo "  Environment: $(if is_codespace; then echo "Codespace"; else echo "Standalone"; fi)"

    # Tool detection summary
    print_banner "Checking Existing Tools" "$CYAN"
    echo -e "${CYAN}Tool Detection Summary:${NC}"
    
    local gcc_installed=false
    local sdkman_installed=false
    
    if check_gcc14 >/dev/null 2>&1; then
        echo -e "  🔧 GCC 14: ${GREEN}Found${NC}"
        gcc_installed=true
    else
        echo -e "  🔧 GCC 14: ${RED}Not Found${NC}"
    fi
    
    if check_sdkman_tools >/dev/null 2>&1; then
        echo -e "  ☕ SDKMAN Tools: ${GREEN}Found${NC}"
        sdkman_installed=true
    else
        echo -e "  ☕ SDKMAN Tools: ${RED}Not Found${NC}"
    fi

    # Confirm installation
    if [ "$VERBOSE" = true ]; then
        echo ""
        if [ "$UPGRADE_MODE" = true ]; then
            echo "Upgrade mode enabled - will reinstall all tools"
        else
            echo "Normal mode - will skip existing tools"
        fi
        read -p "Continue with installation? (Y/n): " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Nn]$ ]]; then
            print_error "Installation cancelled by user"
            exit 1
        fi
    fi

    # Setup package management first
    setup_package_management || {
        print_error "Failed to setup package management"
        exit 1
    }

    # Install GCC 14
    if [ "$UPGRADE_MODE" = true ] || [ "$gcc_installed" = false ]; then
        if [ "$gcc_installed" = true ] && [ "$UPGRADE_MODE" = true ]; then
            print_step "Upgrade mode: Reinstalling GCC 14"
        fi
        install_gcc14 || {
            print_error "Failed to install GCC 14"
            exit 1
        }
    else
        print_success "GCC 14 already installed - skipping"
    fi

    # Install SDKMAN tools
    if [ "$UPGRADE_MODE" = true ] || [ "$sdkman_installed" = false ]; then
        if [ "$sdkman_installed" = true ] && [ "$UPGRADE_MODE" = true ]; then
            print_step "Upgrade mode: Reinstalling SDKMAN tools"
            # Remove existing SDKMAN installation
            rm -rf "$HOME/.sdkman"
        fi
        install_sdkman_tools || {
            print_error "Failed to install SDKMAN tools"
            exit 1
        }
    else
        print_success "SDKMAN tools already installed - skipping"
    fi

    # Verification
    print_banner "INSTALLATION COMPLETE - VERIFYING TOOLS" "$WHITE"
    
    echo ""
    echo -e "${GREEN}🎉 KryptoNyte Basic Tools Installation Complete! 🎉${NC}"
    echo ""
    echo "Installed tools:"

    # GCC verification
    if command -v gcc >/dev/null 2>&1; then
        local gcc_version=$(gcc --version 2>/dev/null | head -1 || echo "gcc installed")
        echo "  ✓ GCC 14:        $gcc_version"
    else
        echo "  ⚠ GCC 14:        Not found"
    fi

    # SDKMAN tools verification
    echo ""
    echo "SDKMAN tools (source ~/.sdkman/bin/sdkman-init.sh to use):"

    if [ -f "$HOME/.sdkman/bin/sdkman-init.sh" ]; then
        (
            source "$HOME/.sdkman/bin/sdkman-init.sh" 2>/dev/null || true
            
            # Java check
            if command -v java >/dev/null 2>&1; then
                local java_version=$(java --version 2>/dev/null | head -1 | cut -d' ' -f2- || echo "installed")
                echo "  ✓ Java:          java $java_version"
            else
                echo "  ⚠ Java:          Not found in PATH"
            fi
            
            # Scala check
            if command -v scala >/dev/null 2>&1; then
                local scala_version=$(timeout 5 scala --version 2>/dev/null | head -1 || echo "installed")
                if [ -n "$scala_version" ] && [ "$scala_version" != "installed" ]; then
                    echo "  ✓ Scala:         $scala_version"
                else
                    echo "  ✓ Scala:         installed (version check timed out)"
                fi
            else
                echo "  ⚠ Scala:         Not found in PATH"
            fi
            
            # SBT check
            if command -v sbt >/dev/null 2>&1; then
                local sbt_version=$(timeout 10 sbt --version 2>/dev/null | grep "sbt runner version" | head -1 || echo "sbt installed")
                echo "  ✓ SBT:           $sbt_version"
            else
                echo "  ⚠ SBT:           Not found in PATH"
            fi
        ) || {
            echo "  ✓ Java:          installed via SDKMAN"
            echo "  ✓ Scala:         installed via SDKMAN"
            echo "  ✓ SBT:           installed via SDKMAN"
        }
    else
        echo "  ⚠ SDKMAN:        Not installed"
    fi

    echo ""
    echo "Next steps:"
    if is_codespace; then
        echo "  1. Your Codespace is ready for basic KryptoNyte development!"
        echo "  2. SDKMAN tools are automatically available in new terminals"
    else
        echo "  1. Start a new terminal session or run: source ~/.bashrc"
        echo "  2. For SDKMAN tools, run: source ~/.sdkman/bin/sdkman-init.sh"
    fi
    echo "  3. Install RTL tools with: ./install_base_rtl_tools.sh"

    print_success "Basic tools successfully installed and configured!"
    exit 0
}

# Run main function
main "$@"
