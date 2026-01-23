#!/bin/bash

#######################################
# KryptoNyte RTL Development Tools Installation
# Installs Verilator, FIRRTL, OSS-CAD Suite, Node.js, sv2v, and Graphics tools
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
            echo "KryptoNyte RTL Development Tools Installation"
            echo ""
            echo "Options:"
            echo "  --with-sudo    Use sudo for commands requiring elevated privileges"
            echo "  --quiet        Reduce output verbosity"
            echo "  --upgrade      Force upgrade/reinstall of existing tools"
            echo "  --help, -h     Show this help message"
            echo ""
            echo "This script installs RTL development tools:"
            echo "  - Verilator HDL simulator"
            echo "  - FIRRTL tools (firtool)"
            echo "  - OSS-CAD Suite (Yosys, nextpnr, etc.)"
            echo "  - Node.js and netlistsvg"
            echo "  - sv2v SystemVerilog converter"
            echo "  - Graphics tools (rsvg-convert, Inkscape)"
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

# Function to check if Verilator is installed
check_verilator() {
    if command -v verilator >/dev/null 2>&1; then
        local version=$(verilator --version 2>/dev/null | head -1)
        print_step "Found Verilator: $version"
        return 0
    else
        print_step "Verilator not found"
        return 1
    fi
}

# Function to check if firtool is installed
check_firtool() {
    if command -v firtool >/dev/null 2>&1; then
        local version=$(firtool --version 2>/dev/null | head -1)
        print_step "Found firtool: $version"
        return 0
    else
        print_step "firtool not found"
        return 1
    fi
}

# Function to check if OSS-CAD Suite is installed
check_oss_cad_suite() {
    if [ -d "/opt/oss-cad-suite" ] && command -v yosys >/dev/null 2>&1; then
        local version=$(yosys -V 2>/dev/null | head -1)
        print_step "Found OSS-CAD Suite: $version"
        return 0
    else
        print_step "OSS-CAD Suite not found"
        return 1
    fi
}

# Function to check if Node.js and netlistsvg are installed
check_nodejs_tools() {
    if command -v node >/dev/null 2>&1 && command -v netlistsvg >/dev/null 2>&1; then
        local node_version=$(node --version 2>/dev/null)
        local npm_version=$(npm --version 2>/dev/null)
        print_step "Found Node.js: $node_version (npm: $npm_version)"
        print_step "Found netlistsvg: installed"
        return 0
    else
        print_step "Node.js tools not found or incomplete"
        return 1
    fi
}

# Function to check if sv2v is installed
check_sv2v() {
    if command -v sv2v >/dev/null 2>&1; then
        local version=$(sv2v --version 2>/dev/null)
        print_step "Found sv2v: $version"
        return 0
    else
        print_step "sv2v not found"
        return 1
    fi
}

# Function to check if graphics tools are installed
check_graphics_tools() {
    if command -v rsvg-convert >/dev/null 2>&1 && command -v inkscape >/dev/null 2>&1; then
        local rsvg_version=$(rsvg-convert --version 2>/dev/null | head -1)
        local inkscape_version=$(inkscape --version 2>/dev/null | head -1)
        print_step "Found graphics tools:"
        print_step "  rsvg-convert: $rsvg_version"
        print_step "  inkscape: $inkscape_version"
        return 0
    else
        print_step "Graphics tools not found or incomplete"
        return 1
    fi
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

# Function to install Verilator
install_verilator() {
    print_banner "INSTALLING VERILATOR HDL SIMULATOR" "$YELLOW"

    print_step "Installing Verilator from package manager"
    run_cmd apt install -y verilator || {
        print_error "Failed to install Verilator"
        return 1
    }

    print_success "Verilator installed"
    verilator --version | head -1
    return 0
}

# Function to install FIRRTL tools
install_firtool() {
    print_banner "INSTALLING FIRRTL TOOLS (FIRTOOL)" "$PURPLE"

    print_step "Fetching latest FIRRTL tools release"
    local firtool_url=$(curl -s https://api.github.com/repos/llvm/circt/releases/latest | \
        jq -r '.assets[] | select(.name == "firrtl-bin-linux-x64.tar.gz") | .browser_download_url')

    if [ -z "$firtool_url" ] || [ "$firtool_url" = "null" ]; then
        print_error "Failed to fetch FIRRTL tools release information"
        return 1
    fi

    print_step "Downloading FIRRTL tools"
    if ! wget --no-check-certificate "$firtool_url" -O /tmp/firtool.tar.gz; then
        print_error "Failed to download FIRRTL tools"
        return 1
    fi

    print_step "Extracting FIRRTL tools"
    mkdir -p /tmp/firtool
    if ! tar -xzf /tmp/firtool.tar.gz -C /tmp/firtool; then
        print_error "Failed to extract FIRRTL tools archive"
        rm -rf /tmp/firtool /tmp/firtool.tar.gz
        return 1
    fi

    print_step "Locating firtool binary"
    local firtool_path=$(find /tmp/firtool -type f -name firtool -executable 2>/dev/null | head -1)

    if [ -z "$firtool_path" ]; then
        print_error "firtool binary not found in extracted archive"
        rm -rf /tmp/firtool /tmp/firtool.tar.gz
        return 1
    fi

    print_step "Installing firtool to /usr/local/bin"
    if run_cmd mv "$firtool_path" /usr/local/bin/firtool; then
        run_cmd chmod +x /usr/local/bin/firtool
        print_success "FIRRTL tools (firtool) installed"
    else
        print_error "Failed to install firtool to /usr/local/bin"
        rm -rf /tmp/firtool /tmp/firtool.tar.gz
        return 1
    fi

    # Cleanup
    rm -rf /tmp/firtool /tmp/firtool.tar.gz

    # Verify installation
    if command -v firtool >/dev/null 2>&1; then
        firtool --version | head -1
        return 0
    else
        print_error "firtool installation verification failed"
        return 1
    fi
}

# Function to install OSS-CAD Suite
install_oss_cad_suite() {
    print_banner "INSTALLING OSS-CAD SUITE (YOSYS, NEXTPNR, ETC.)" "$CYAN"

    print_step "Fetching latest OSS-CAD Suite release"
    local oss_cad_url=$(curl -s https://api.github.com/repos/YosysHQ/oss-cad-suite-build/releases/latest | \
        jq -r '.assets[] | select(.name | contains("linux-x64")) | .browser_download_url') || {
        print_error "Failed to fetch OSS-CAD Suite release information"
        return 1
    }

    print_step "Downloading OSS-CAD Suite (this may take a while)"
    wget --no-check-certificate "$oss_cad_url" -O /tmp/oss-cad-suite.tar.xz || {
        print_error "Failed to download OSS-CAD Suite"
        return 1
    }

    print_step "Installing OSS-CAD Suite to /opt/oss-cad-suite"
    run_cmd mkdir -p /opt/oss-cad-suite && \
    run_cmd tar -xf /tmp/oss-cad-suite.tar.xz -C /opt/oss-cad-suite --strip-components=1 && \
    rm /tmp/oss-cad-suite.tar.xz || {
        print_error "Failed to install OSS-CAD Suite"
        return 1
    }

    print_success "OSS-CAD Suite installed"
    /opt/oss-cad-suite/bin/yosys --version | head -1
    return 0
}

# Function to install Node.js and netlistsvg
install_nodejs_tools() {
    print_banner "INSTALLING NODE.JS AND NETLISTSVG" "$GREEN"

    print_step "Installing Node.js 22.x"
    curl -fsSL https://deb.nodesource.com/setup_22.x | run_cmd bash - || {
        print_error "Failed to add Node.js repository"
        return 1
    }

    run_cmd apt install -y nodejs && run_cmd apt clean || {
        print_error "Failed to install Node.js"
        return 1
    }

    print_step "Updating npm to latest version"
    run_cmd npm install -g npm@latest || {
        print_error "Failed to update npm"
        return 1
    }

    print_step "Installing netlistsvg globally"
    run_cmd npm install -g netlistsvg || {
        print_error "Failed to install netlistsvg"
        return 1
    }

    print_success "Node.js and netlistsvg installed"
    node --version
    npm --version
    return 0
}

# Function to install sv2v
install_sv2v() {
    print_banner "INSTALLING SV2V SYSTEMVERILOG CONVERTER" "$BLUE"

    print_step "Fetching latest sv2v release"
    local sv2v_url=$(curl -s https://api.github.com/repos/zachjs/sv2v/releases/latest | \
        jq -r '.assets[] | select(.name == "sv2v-Linux.zip") | .browser_download_url')

    if [ -z "$sv2v_url" ] || [ "$sv2v_url" = "null" ]; then
        print_error "Failed to fetch sv2v release information"
        return 1
    fi

    print_step "Downloading sv2v"
    if ! wget "$sv2v_url" -O /tmp/sv2v.zip; then
        print_error "Failed to download sv2v"
        return 1
    fi

    print_step "Extracting sv2v"
    mkdir -p /tmp/sv2v
    if ! unzip /tmp/sv2v.zip -d /tmp/sv2v; then
        print_error "Failed to extract sv2v archive"
        rm -rf /tmp/sv2v /tmp/sv2v.zip
        return 1
    fi

    print_step "Locating sv2v binary"
    local sv2v_path=$(find /tmp/sv2v -type f -name sv2v -executable 2>/dev/null | head -1)

    if [ -z "$sv2v_path" ]; then
        print_error "sv2v binary not found in extracted archive"
        rm -rf /tmp/sv2v /tmp/sv2v.zip
        return 1
    fi

    print_step "Installing sv2v to /usr/local/bin"
    if run_cmd mv "$sv2v_path" /usr/local/bin/sv2v; then
        run_cmd chmod +x /usr/local/bin/sv2v
        print_success "sv2v installed"
    else
        print_error "Failed to install sv2v to /usr/local/bin"
        rm -rf /tmp/sv2v /tmp/sv2v.zip
        return 1
    fi

    # Cleanup
    rm -rf /tmp/sv2v /tmp/sv2v.zip

    # Verify installation
    if command -v sv2v >/dev/null 2>&1; then
        sv2v --version
        return 0
    else
        print_error "sv2v installation verification failed"
        return 1
    fi
}

# Function to install graphics tools
install_graphics_tools() {
    print_banner "INSTALLING GRAPHICS TOOLS (RSVG-CONVERT, INKSCAPE)" "$PURPLE"

    print_step "Updating package lists"
    run_cmd apt update || {
        print_error "Failed to update package lists"
        return 1
    }

    print_step "Installing graphics tools"
    run_cmd apt install -y \
        librsvg2-bin \
        inkscape \
        && run_cmd apt clean || {
        print_error "Failed to install graphics tools"
        return 1
    }

    print_success "Graphics tools installed"
    rsvg-convert --version | head -1
    inkscape --version | head -1
    return 0
}

# Function to setup PATH
setup_path() {
    print_step "Setting up PATH for current session"
    export PATH="/usr/local/bin:$PATH"
    export PATH="/opt/oss-cad-suite/bin:$PATH"

    print_step "Updating ~/.bashrc with PATH additions"
    cat >> ~/.bashrc << 'EOF'
# KryptoNyte RTL Development Tools
if [[ ":$PATH:" != *":/usr/local/bin:"* ]]; then
    export PATH="/usr/local/bin:$PATH"
fi
if [[ ":$PATH:" != *":/opt/oss-cad-suite/bin:"* ]]; then
    export PATH="/opt/oss-cad-suite/bin:$PATH"
fi
EOF
}

# Main function
main() {
    print_banner "KRYPTONYTE RTL DEVELOPMENT TOOLS INSTALLATION" "$WHITE"
    
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

    echo -e "${CYAN}This will install RTL development tools for KryptoNyte${NC}"
    echo ""
    echo "Configuration:"
    echo "  Using sudo: $USE_SUDO"
    echo "  Verbose output: $VERBOSE"
    echo "  Upgrade mode: $UPGRADE_MODE"
    echo "  Environment: $(if is_codespace; then echo "Codespace"; else echo "Standalone"; fi)"

    # Tool detection summary
    print_banner "Checking Existing Tools" "$CYAN"
    echo -e "${CYAN}Tool Detection Summary:${NC}"
    
    local verilator_installed=false
    local firtool_installed=false
    local oss_cad_installed=false
    local nodejs_installed=false
    local sv2v_installed=false
    local graphics_installed=false
    
    if check_verilator >/dev/null 2>&1; then
        echo -e "  🔧 Verilator: ${GREEN}Found${NC}"
        verilator_installed=true
    else
        echo -e "  🔧 Verilator: ${RED}Not Found${NC}"
    fi
    
    if check_firtool >/dev/null 2>&1; then
        echo -e "  🔥 FIRRTL Tools: ${GREEN}Found${NC}"
        firtool_installed=true
    else
        echo -e "  🔥 FIRRTL Tools: ${RED}Not Found${NC}"
    fi
    
    if check_oss_cad_suite >/dev/null 2>&1; then
        echo -e "  🛠️  OSS-CAD Suite: ${GREEN}Found${NC}"
        oss_cad_installed=true
    else
        echo -e "  🛠️  OSS-CAD Suite: ${RED}Not Found${NC}"
    fi
    
    if check_nodejs_tools >/dev/null 2>&1; then
        echo -e "  📦 Node.js Tools: ${GREEN}Found${NC}"
        nodejs_installed=true
    else
        echo -e "  📦 Node.js Tools: ${RED}Not Found${NC}"
    fi
    
    if check_sv2v >/dev/null 2>&1; then
        echo -e "  🔄 sv2v: ${GREEN}Found${NC}"
        sv2v_installed=true
    else
        echo -e "  🔄 sv2v: ${RED}Not Found${NC}"
    fi
    
    if check_graphics_tools >/dev/null 2>&1; then
        echo -e "  🎨 Graphics Tools: ${GREEN}Found${NC}"
        graphics_installed=true
    else
        echo -e "  🎨 Graphics Tools: ${RED}Not Found${NC}"
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

    # Setup package management and PATH
    setup_package_management || {
        print_error "Failed to setup package management"
        exit 1
    }
    
    setup_path

    # Install tools
    local install_count=0
    local skip_count=0

    # Install Verilator
    if [ "$UPGRADE_MODE" = true ] || [ "$verilator_installed" = false ]; then
        if [ "$verilator_installed" = true ] && [ "$UPGRADE_MODE" = true ]; then
            print_step "Upgrade mode: Reinstalling Verilator"
        fi
        if install_verilator; then
            ((install_count++))
        else
            print_error "Failed to install Verilator"
            exit 1
        fi
    else
        print_success "Verilator already installed - skipping"
        ((skip_count++))
    fi

    # Install FIRRTL tools
    if [ "$UPGRADE_MODE" = true ] || [ "$firtool_installed" = false ]; then
        if [ "$firtool_installed" = true ] && [ "$UPGRADE_MODE" = true ]; then
            print_step "Upgrade mode: Reinstalling FIRRTL tools"
            rm -f /usr/local/bin/firtool
        fi
        if install_firtool; then
            ((install_count++))
        else
            print_error "Failed to install FIRRTL tools"
            exit 1
        fi
    else
        print_success "FIRRTL tools already installed - skipping"
        ((skip_count++))
    fi

    # Install OSS-CAD Suite
    if [ "$UPGRADE_MODE" = true ] || [ "$oss_cad_installed" = false ]; then
        if [ "$oss_cad_installed" = true ] && [ "$UPGRADE_MODE" = true ]; then
            print_step "Upgrade mode: Reinstalling OSS-CAD Suite"
            run_cmd rm -rf /opt/oss-cad-suite
        fi
        if install_oss_cad_suite; then
            ((install_count++))
        else
            print_error "Failed to install OSS-CAD Suite"
            exit 1
        fi
    else
        print_success "OSS-CAD Suite already installed - skipping"
        ((skip_count++))
    fi

    # Install Node.js tools
    if [ "$UPGRADE_MODE" = true ] || [ "$nodejs_installed" = false ]; then
        if [ "$nodejs_installed" = true ] && [ "$UPGRADE_MODE" = true ]; then
            print_step "Upgrade mode: Reinstalling Node.js tools"
        fi
        if install_nodejs_tools; then
            ((install_count++))
        else
            print_error "Failed to install Node.js tools"
            exit 1
        fi
    else
        print_success "Node.js tools already installed - skipping"
        ((skip_count++))
    fi

    # Install sv2v
    if [ "$UPGRADE_MODE" = true ] || [ "$sv2v_installed" = false ]; then
        if [ "$sv2v_installed" = true ] && [ "$UPGRADE_MODE" = true ]; then
            print_step "Upgrade mode: Reinstalling sv2v"
            rm -f /usr/local/bin/sv2v
        fi
        if install_sv2v; then
            ((install_count++))
        else
            print_error "Failed to install sv2v"
            exit 1
        fi
    else
        print_success "sv2v already installed - skipping"
        ((skip_count++))
    fi

    # Install graphics tools
    if [ "$UPGRADE_MODE" = true ] || [ "$graphics_installed" = false ]; then
        if [ "$graphics_installed" = true ] && [ "$UPGRADE_MODE" = true ]; then
            print_step "Upgrade mode: Reinstalling graphics tools"
        fi
        if install_graphics_tools; then
            ((install_count++))
        else
            print_error "Failed to install graphics tools"
            exit 1
        fi
    else
        print_success "Graphics tools already installed - skipping"
        ((skip_count++))
    fi

    # Verification
    print_banner "INSTALLATION COMPLETE - VERIFYING TOOLS" "$WHITE"
    
    echo ""
    echo -e "${GREEN}🎉 KryptoNyte RTL Development Tools Installation Complete! 🎉${NC}"
    echo ""
    echo "Installation Summary:"
    echo "  Tools installed: $install_count"
    echo "  Tools skipped: $skip_count"
    echo ""
    echo "Installed tools:"

    # Tool verification
    if command -v verilator >/dev/null 2>&1; then
        local verilator_version=$(verilator --version 2>/dev/null | head -1 || echo "verilator installed")
        echo "  ✓ Verilator:     $verilator_version"
    else
        echo "  ⚠ Verilator:     Not found"
    fi

    if command -v firtool >/dev/null 2>&1; then
        local firtool_version=$(firtool --version 2>/dev/null | head -1 || echo "firtool installed")
        echo "  ✓ firtool:       $firtool_version"
    else
        echo "  ⚠ firtool:       Not found"
    fi

    if command -v yosys >/dev/null 2>&1; then
        local yosys_version=$(yosys -V 2>/dev/null | head -1 || echo "yosys installed")
        echo "  ✓ Yosys:         $yosys_version"
    else
        echo "  ⚠ Yosys:         Not found"
    fi

    if command -v node >/dev/null 2>&1; then
        local node_version=$(node --version 2>/dev/null || echo "node installed")
        echo "  ✓ Node.js:       $node_version"
    else
        echo "  ⚠ Node.js:       Not found"
    fi

    if command -v netlistsvg >/dev/null 2>&1; then
        echo "  ✓ netlistsvg:    installed"
    else
        echo "  ⚠ netlistsvg:    Not found"
    fi

    if command -v sv2v >/dev/null 2>&1; then
        local sv2v_version=$(sv2v --version 2>/dev/null | head -1 || echo "sv2v installed")
        echo "  ✓ sv2v:          $sv2v_version"
    else
        echo "  ⚠ sv2v:          Not found"
    fi

    if command -v rsvg-convert >/dev/null 2>&1; then
        local rsvg_version=$(rsvg-convert --version 2>/dev/null | head -1 || echo "rsvg-convert installed")
        echo "  ✓ rsvg-convert:  $rsvg_version"
    else
        echo "  ⚠ rsvg-convert:  Not found"
    fi

    if command -v inkscape >/dev/null 2>&1; then
        local inkscape_version=$(inkscape --version 2>/dev/null | head -1 || echo "inkscape installed")
        echo "  ✓ Inkscape:      $inkscape_version"
    else
        echo "  ⚠ Inkscape:      Not found"
    fi

    echo ""
    echo "Next steps:"
    if is_codespace; then
        echo "  1. Your Codespace is ready for RTL development!"
        echo "  2. Tools are automatically available in new terminals"
    else
        echo "  1. Start a new terminal session or run: source ~/.bashrc"
        echo "  2. All RTL tools should now be in your PATH"
    fi
    echo "  3. Install physical design tools with: ./install_physical_design_tools.sh"

    print_success "RTL development tools successfully installed and configured!"
    exit 0
}

# Run main function
main "$@"
